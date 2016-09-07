(ns notebook.gdoc
  (:require [net.cgrand.enlive-html :as html]
            [hieronymus.core :as hiero]
            [clojure.string :as string]
            [clojure.set :as set]
            [clojure.walk :as walk])
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

(def carons->breves
  {\ǎ \ă
   \ǐ \ĭ
   \ě \ĕ
   \ǒ \ŏ
   \ǔ \ŭ})

(defn caron->breve [s]
  (->> (map char s)
       (map #(get carons->breves % %))
       (string/join)))

(defn walk-strings [f data]
  (walk/prewalk #(if (string? %) (f %) %) data))

(defn swap-carons-for-breves [data]
  (walk-strings caron->breve data))

(defn cjk? [c]
  (let [n (int c)]
    (and (> n 19968) (< n 40908))))

(defn wrap-cjk [data]
  (walk-strings
    (fn [s]
      (let [o (->> (map char s)
                   (partition-by cjk?)
                   (map string/join))]
        (if (= 1 (count o))
          s
          (map
            (fn [seg]
              (if (cjk? (first seg))
                {:tag :span :attrs {:class "cjk"} :content (string/join seg)}
                (string/join seg)))
            o))))
    data))

(defn enrich-el [[bold italic] {:keys [tag content] :as el}]
  (if (not= :p tag)
    el
    (let [txt (html/text el)]
      (if (and (empty? txt)
               (empty? (html/select el [:img])))
        {:tag :div :attrs {:class "spacer"} :content ""}
        (assoc
          el
          :content
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
                      el                                    ; should be markdown'd
                      )))))
            content))))))

(defn ->html [ps]
  (->> ps
       (drop-while #(= :div (:tag %)))
       (reverse)
       (drop-while #(= :div (:tag %)))
       (reverse)
       (html/emit*)
       (string/join)))

(defn flatten-nested-content-lists [data]
  (walk/postwalk
    (fn [el]
      (if (and (map? el) (sequential? (:content el)))
        (if (sequential? (first (:content el)))
          (update el :content first)
          el)
        el))
    data))

(defn fetch-html [gdoc-id]
  (let [res (html/html-resource (URL. (g-dld gdoc-id :html)))
        style (parse-css (first (html/select res [:style])))]
    {:res res
     :style style
     :ps (->> (html/select res [:body])
              (first)
              (:content)
              (map (partial enrich-el style))
              (swap-carons-for-breves)
              (wrap-cjk)
              (flatten-nested-content-lists))}))