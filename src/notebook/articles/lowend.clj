(ns notebook.articles.lowend
  (:require [notebook.wikidata :as wikidata]
            [notebook.wikipedia :as wikipedia]
            [clojure.string :as string]))

#_"Conquering the Lowend"

"https://en.wikipedia.org/wiki/Template:Billboard_Year-End_number_one_singles"

(defn fetch-by-title [title]
  (let [q (wikipedia/get-wikidata-q title)
        r (wikidata/q q)
        pq (:id (wikidata/q-from-claim :performer r))]
    (str
      (string/replace title #"\_" " ")
      ", "
      (wikidata/q-from-claim :proper-name (wikidata/q pq)))))