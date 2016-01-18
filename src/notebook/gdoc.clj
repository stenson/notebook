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
          id fmt))

(defn read-html [url-s]
  (html/html-resource (URL. url-s)))

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
                (map p->md)
                (string/join "\n\n")
                (str " "))]
    (hiero/parse md {})))

(defn parse [style gid]
  (case style
    :html (html->hieronymus->html (read-html (g-dld gid "html")))
    :txt (hiero/parse (str " " (slurp (g-dld gid "txt"))) {})))

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