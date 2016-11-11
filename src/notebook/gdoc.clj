(ns notebook.gdoc
  (:require [net.cgrand.enlive-html :as html]
            [hieronymus.core :as hiero]
            [clojure.string :as string]
            [clojure.set :as set]
            [clojure.walk :as walk]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs])
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

(defn split&trim [s re]
  (map string/trim (string/split s re)))

(defn style-string->map [style-string]
  (->> (split&trim style-string #";")
       (map #(split&trim % #":"))
       (map (fn [[k v]]
              [(keyword k)
               (if (and (string? v) (re-find #"px$" v))
                 (Float/parseFloat (string/replace v #"px" ""))
                 v)]))
       (into {})))

(defn copy [uri file]
  (with-open [in (io/input-stream uri)
              out (io/output-stream file)]
    (io/copy in out)))

(defn split-els [els]
  (partition-by #(= :hr (:tag %)) els))

(defn save-src [src {:keys [site image-dir save]} new-name]
  (let [relative-name (str (if image-dir (str image-dir "/")) new-name)]
    (if save
      (do
        (fs/mkdirs (str "sites/" site))
        (with-open [in (io/input-stream src)
                    out (io/output-stream (str "sites/" site "/" relative-name))]
          (io/copy in out)
          relative-name))
      relative-name)))

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

(defn enrich-el [[bold italic] img-options {:keys [tag content] :as el}]
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
              (let [link (html/select el [:a])]
                (if (not (empty? link))
                  (let [a (first link)
                        href (get-in a [:attrs :href])
                        q (get (url->params href) "q")]
                    {:tag :a
                     :attrs {:href q}
                     :content (html/text a)})
                  (let [imgs (html/select el [:img])]
                    (if (not (empty? imgs))
                      (let [img (first imgs)
                            src (:src (:attrs img))
                            style (style-string->map (:style (:attrs img)))]
                        {:tag :img
                         :attrs {:aspect (/ (:width style) (:height style))
                                 :src (if img-options
                                        (save-src
                                          src
                                          img-options
                                          (str (subs (fs/base-name src) 0 8) ".jpg"))
                                        src)}})
                      (if (= (:class attrs) bold)
                        {:tag :strong
                         :attrs nil
                         :content content}
                        (if (= (:class attrs) italic)
                          {:tag :em :attrs nil :content content}
                          el                                ; should be markdown'd
                          )))))))
            content))))))

(defn trim-spacers [els]
  (->> els
       (drop-while #(= :div (:tag %)))
       (reverse)
       (drop-while #(= :div (:tag %)))
       (reverse)))

(defn ->html [ps]
  (->> ps
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

(defn fetch-html
  ([gdoc-id]
    (fetch-html gdoc-id {}))
  ([gdoc-id img-options]
   (let [res (html/html-resource (URL. (g-dld gdoc-id :html)))
         style (try
                 (parse-css (first (html/select res [:style])))
                 (catch Exception _
                   ["x1" "x2"]))]
     {:options img-options
      :res res
      :style style
      :els (->> (html/select res [:body])
                (first)
                (:content)
                (map (partial enrich-el style img-options))
                (swap-carons-for-breves)
                (wrap-cjk)
                (flatten-nested-content-lists)
                (split-els)
                (map trim-spacers)
                (remove #(= :hr (:tag (first %)))))})))