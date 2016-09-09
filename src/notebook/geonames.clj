(ns notebook.geonames
  (:require [notebook.http :as http]))

(defn geonames [q]
  (let [body (http/fetch-json
               "http://api.geonames.org/search"
               {:q q :username "robstenson" :type "json" :maxRows 1})
        ll (-> (:geonames body)
               (first)
               (select-keys [:lat :lng])
               (vals)
               (reverse))]
    (map #(Float/parseFloat %) ll)))