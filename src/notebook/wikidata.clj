(ns notebook.wikidata
  (:require [cemerick.url :as url]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [puget.printer :as puget]))

;https://www.wikidata.org/w/api.php?action=wbgetentities&ids=Q330149&languages=en&format=json

(def props
  {:birth-place [:P19 :numeric-id]
   :coords [:P625 (fn [r] [(:longitude r)
                           (:latitude r)])]
   :proper-name [:P373 identity]})

(defn search [query]
  (let [resp (->> {:action "wbsearchentities"
                   :language "en"
                   :format "json"
                   :search query}
                  (url/map->query)
                  (format "https://www.wikidata.org/w/api.php?%s")
                  (client/get))
        data (json/read-str (:body resp) :key-fn keyword)]
    (map :id (:search data))))

(defn q [qnum]
  (let [as-key (keyword qnum)
        resp (->> {:action "wbgetentities"
                   :languages "en"
                   :format "json"
                   :ids qnum}
                  (url/map->query)
                  (format "https://www.wikidata.org/w/api.php?%s")
                  (client/get))
        data (json/read-str (:body resp) :key-fn keyword)]
    (:claims (get (:entities data) as-key))))

(defn q-from-claim [prop-key record]
  (let [[pk func] (get props prop-key)
        claim (first (get record pk))
        raw (get-in claim [:mainsnak :datavalue :value])]
    (func raw)))

(defn geocode [query]
  (let [f (format "tmp/_geocode_%s" query)]
    (try
      (read-string (slurp f))
      (catch Exception _
        (let [pair (->> (search query)
                        (map q)
                        (map (fn [rec]
                               (q-from-claim :coords rec)))
                        (remove #(some nil? %))
                        (first))]
          (spit f (with-out-str (pr pair)))
          pair)))))

(defn birth-place [qnum]
  (let [person (q qnum)
        birth-q (q-from-claim :birth-place person)
        birth-place (q (str "Q" birth-q))]
    [(q-from-claim :proper-name birth-place)
     (q-from-claim :coords birth-place)]))