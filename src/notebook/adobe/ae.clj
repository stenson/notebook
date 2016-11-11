(ns notebook.adobe.ae
  (:require [clojure.string :as string]
            [me.raynes.fs :as fs]))

"app.beginUndoGroup(\"WTF\");\nvar comp = app.project.activeItem;\nvar newTextLayer = comp.layers.addText(\"ZZZZ\");\nnewTextLayer.property(\"ADBE Transform Group\").property(\"ADBE Position\").setValue([100,100]);\nvar textProp = newTextLayer.property(\"Source Text\");\nvar textDocument = textProp.value;\ntextDocument.resetCharStyle();\ntextDocument.fontSize = 136;\ntextDocument.fillColor = [0, 1, 0];\ntextDocument.strokeColor = [0, 1, 0];\ntextDocument.strokeWidth = 0;\ntextDocument.font = \"Hobeaux\";\ntextDocument.strokeOverfill = false;\ntextDocument.applyStroke = false;\ntextDocument.applyFill = true;\ntextDocument.text = \"Yo yo ma\";\ntextDocument.justification = ParagraphJustification.LEFT_JUSTIFY;\ntextProp.setValue(textDocument);\napp.endUndoGroup();\ntextProp.setValueAtTime(0, new TextDocument(\"YO YO MAR\"));\ntextProp.setValueAtTime(1, new TextDocument(\"IS THE BEST\"));"

(def lowercase (map char (range 97 123)))

(defn rand-var [prefix]
  (->> (repeatedly 8 #(rand-nth lowercase))
       (map str)
       (string/join "")
       (format "%s_%s" prefix)))

(defn undo-group [els]
  (flatten [(format "app.beginUndoGroup(\"%s\")" (rand-var "ugroup"))
            els
            (format "app.endUndoGroup()")]))

(defn wrap-scope [els]
  (flatten ["(function(){"
            els
            "})()"]))

(defn as-code-string [els]
  (->> els
       (map #(str % ";"))
       (string/join "\n")))

(defn set-pos [v x y]
  (format "%s.property(\"ADBE Transform Group\").property(\"ADBE Position\").setValue([%s,%s])"
          v x y))

(defn text [{:keys [fontSize font fillColor strokeColor strokeWidth justification]}
            text]
  (->> ["var comp = app.project.activeItem"
        (str "var l = " "comp.layers.addText(\"XXX\")")
        (set-pos "l" "(comp.width * 0.5)" "(comp.height * 0.5)")
        "tp = l.property(\"Source Text\")"
        "td = tp.value"
        "td.resetCharStyle()"
        (format "td.text = '%s'" text)
        (format "td.fontSize = %s" (or fontSize 100))
        (format "td.font = '%s'" (or font "Hobeaux"))
        (apply format "td.fillColor = [%s, %s, %s]" (or fillColor [1 0 0]))
        (apply format "td.strokeColor = [%s, %s, %s]" (or strokeColor fillColor [1 0 0]))
        (format "td.strokeWidth = %s" (or strokeWidth 0))
        "td.strokeOverfill = false"
        "td.applyStroke = false"
        "td.applyFill = true"
        (format "td.justification = %s"
                (case (or justification :center)
                  :center "ParagraphJustification.CENTER_JUSTIFY"
                  :left "ParagraphJustification.LEFT_JUSTIFY"
                  :right "ParagraphJustification.RIGHT_JUSTIFY"))
        "tp.setValue(td)"
        "var h = l.sourceRectAtTime(0,true).height"
        #_(set-pos "l" "(comp.width * 0.5)" "(comp.height * 0.5 + h / 2)")
        ]
       (undo-group)
       (wrap-scope)))

(defn save [script]
  (->> (as-code-string script)
       (spit (fs/expand-home "~/Documents/Adobe Scripts/Test1.jsx"))))