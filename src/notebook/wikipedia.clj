(ns notebook.wikipedia
  (:require [notebook.http :as http]
            [clojure.string :as string]))

;https://en.wikipedia.org/w/api.php?action=query&prop=pageprops&titles=Mr.+Brightside&ppprop=wikibase_item

(def api "https://en.wikipedia.org/w/api.php")

(defn extract-infobox [lines]
  (let [info (->> (filter #(re-find #"^\|\s?[A-Za-z_]" %) lines)
                  (filter #(re-find #"\=" %))
                  (string/join "\n")
                  #_(map (fn [line]
                           (let [xs (map string/trim (string/split line #"="))]
                             [(keyword (string/replace (first xs) #"^\|([\s]+)?" ""))
                              (string/join "=" (rest xs))])))
                  #_(into {}))]
    (->> (string/split info #"\|")
         (map string/trim)
         (map #(string/split % #"="))
         (filter #(= (count %) 2))
         (map (fn [[k v]] [(keyword (string/trim k))
                           (string/trim v)]))
         (vec)
         (into {}))))

(defn content [title]
  (->> (http/fetch-json
         api
         {:action "query"
          :prop "revisions"
          :rvprop "content"
          :format "json"
          :titles title})
       (:query) (:pages)
       (first) (second)
       (:revisions) (first)
       (:*)))

(defn get-wikidata-q [title]
  (->> (http/fetch-json
         api
         {:action "query"
          :format "json"
          :prop "pageprops"
          :titles title
          :ppprop "wikibase_item"})
       (:query) (:pages)
       (first) (second)
       (:pageprops) (:wikibase_item)))
