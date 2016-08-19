(ns notebook.hiero
  (:require [hieronymus.core :as hiero]
            [clojure.string :as string]))

(defn reassemble [txt]
  (->> (string/split-lines txt)
       (map string/trim)
       (string/join " ")))

(defn parse-p [txt]
  (hiero/parse (str " " (reassemble txt)) {}))

(defn as-txt [ps]
  (str " " (string/join "\n" (map reassemble (drop 1 ps)))))

(defn <-inline [ps]
  (-> (as-txt ps)
      (hiero/parse {})
      (:html)))

(defn <-txt [txt]
  (-> (str " " txt)
      (hiero/parse {})
      (:html)))

(defn slurp&parse [site f]
  (->> (format "sites/%s/txt/%s.txt" site (name f))
       (slurp)
       (<-txt)))