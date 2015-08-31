(ns notebook.sparql
  (:require [yesparql.sparql :as sparql]
            [yesparql.generate :as yg]
            [yesparql.queryfile-parser :as yqp]
            [puget.printer :refer [cprint]])
  (:import (java.net URI)))

(defn build-query [options query-name]
  (->> (slurp (format "src/notebook/rq/%s.rq" query-name))
       (format "-- name: %s\n%s" query-name)
       (yqp/parse-tagged-queries)
       (map #(yg/generate-var % options))))

(def dbpedia (partial build-query {:connection "http://dbpedia.org/sparql"}))

(defn fetch-with-resource [f resource]
  (let [uri (URI. (format "http://dbpedia.org/resource/%s" resource))
        result (f [{:bindings {"subject" uri}}])]
    (sparql/result->clj result)))

;(def intellectuals (sparql/dbpedia "intellectuals"))