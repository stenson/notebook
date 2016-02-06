(ns notebook.css
  (:require [clojure.string :as string]))

(def pixelable-style-props
  #{:font-size
    :letter-spacing
    :line-height
    :height
    :width
    :top
    :left
    :bottom
    :right
    :margin-top
    :margin-left
    :margin-bottom
    :margin-right
    :padding-top
    :padding-left
    :padding-bottom
    :padding-right})

(defn ß [hash]
  (->> hash
       (remove #(nil? (second %)))
       (map
         (fn [[k v]]
           (str
             (name k)
             ":"
             (if (and (number? v) (contains? pixelable-style-props k))
               (str v "px")
               v))))
       (string/join ";")))

(defn ß+ [hash]
  {:style (ß hash)})