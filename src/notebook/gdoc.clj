(ns notebook.gdoc
  (:require [net.cgrand.enlive-html :as html]
            [hieronymus.core :as hiero]
            [clojure.string :as string]
            [clojure.set :as set])
  (:import (java.net URL)))

(defn g-dld [id fmt]
  (format (string/join
            "" ["https://docs.google.com/feeds/download/"
                "documents/export/Export?id=%s&exportFormat=%s"])
          id
          (name fmt)))

(defn read-html [url-s]
  (let [res (html/html-resource (URL. url-s))]
    res))

(defn read-docx [slug]
  (let [docx (slurp (URL. (g-dld slug "doc")))]
    (spit "tmp.doc" docx)))

(defn breakdown-span [{:keys [attrs content]}]
  (let [classes (into #{} (map keyword (string/split (:class attrs) #" ")))]
    {:classes classes
     :content (apply str content)}))

(defn p->md [p]
  (let [spans (map breakdown-span (html/select p [:span]))
        classes (map :classes spans)
        sentinel (first (set/difference
                          (apply set/union classes)
                          (apply set/intersection classes)))]
    (->> spans
         (map (fn [{:keys [classes content]}]
                (if (contains? classes sentinel)
                  (format "_%s_" content)
                  content)))
         (string/join ""))))

(defn html->hieronymus->html [h]
  (let [md (->> (html/select h [:p])
                ;(map p->md)
                (string/join "\n\n")
                (str " "))]
    (hiero/parse md {})))

(defn html-as-hieronymized-text [h]
  (->> (html/select h [:p])
       (map (fn [el]
              (let [t (html/text el)]
                (if (not (empty? t))
                  t
                  (when-let [img (first (html/select el [:img]))]
                    (let [{:keys [style src]} (:attrs img)]
                      (format "ƒ«img:%s»(ß:max-width:700px)" src)))))))
       (remove nil?)
       (string/join "\n\n")))

(defn parse
  ([style gid]
    (parse style gid true))
  ([style gid add-space?]
   (case style
     ;:html (html->hieronymus->html (read-html (g-dld gid "html")))
     :html (-> (g-dld gid "html")
               (read-html)
               (html-as-hieronymized-text)
               (hiero/parse {}))
     :txt (hiero/parse (str (if add-space? " ") (slurp (g-dld gid "txt"))) {}))))

(defn align-to-cols [cols txt]
  (->> (string/split txt #"\s")
       (reduce
         (fn [acc s]
           (let [lines (butlast acc)
                 line (last acc)]
             (if (> (count line) cols)
               (concat lines [(string/trim line) s])
               (concat lines [(str line " " s)]))))
         [""])
       (string/join "\n")))

(defn as-clj-str-collection [gid]
  (->> (g-dld gid "txt")
       (slurp)
       (string/split-lines)
       (remove empty?)
       (mapv (partial align-to-cols 40))))

;;;;;;;;;;;;;;;;;;

(defn url->params [u]
  (->> (string/split (.getQuery (URL. u)) #"&")
       (map #(string/split % #"="))
       (into {})))

(defn parse-css [css]
  (let [unwrap (fn [xs] (string/replace (first (first xs)) #"^\." ""))
        rules (->> (string/split (first (:content css)) #"\}")
                   (map #(string/split % #"\{"))
                   (filter (fn [[sel]] (re-matches #"\.c.*" sel))))
        bold (filter (fn [[_ rules]] (= rules "font-weight:700")) rules)
        italic (filter (fn [[_ rules]] (= rules "font-style:italic")) rules)]
    [(unwrap bold)
     (unwrap italic)]))

(defn pluck-src [img-node]
  (-> (html/select img-node [:img])
      (first)
      (get-in [:attrs :src])))

(defn process-grafs [[bold italic] gs]
  (->> gs
       (map
         (fn [{:keys [content] :as p}]
           (let [txt (html/text p)]
             (if (empty? txt)
               {:tag :div :attrs {:class "spacer"} :content ""}
               (assoc
                 p :content
                   (map
                     (fn [{:keys [attrs content] :as el}]
                       (let [a-s (html/select el [:a])]
                         (if (not (empty? a-s))
                           (let [a (first a-s)
                                 href (get-in a [:attrs :href])
                                 q (get (url->params href) "q")]
                             {:tag :a
                              :attrs {:href q}
                              :content (html/text a)})
                           (if (= (:class attrs) bold)
                             {:tag :strong
                              :attrs nil
                              :content content}
                             (if (= (:class attrs) italic)
                               {:tag :em :attrs nil :content content}
                               el                           ; should be markdown'd
                               )))))
                     content))))))
       (html/emit*)
       (string/join)))

(defn fetch-html [gdoc-id]
  (let [res (html/html-resource (URL. (g-dld gdoc-id :html)))]
    {:res res
     :style (parse-css (first (html/select res [:style])))
     :ps (html/select res [:p])}))