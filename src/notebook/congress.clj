(ns notebook.congress
  (:require [clojure.data.json :as json]
            [notebook.wikidata :as wikidata]
            [clojure.string :as string]
            [notebook.geojson :as geojson]
            [notebook.colors :as colors]
            [notebook.districts :as districts]
            [me.raynes.fs :as fs]
            [rodeo.core :as rodeo]))

#_(def geocodio-api "8f995738c589587cc7fa9f97aff9d995971f550")

#_(defn geocode [where]
  (let [raw (rodeo/single where :api-key geocodio-api)
        best (:location (first (:results raw)))]
    [(:lng best)
     (:lat best)]))

(defn mapify [person]
  (let [ids (->> (:identifiers person)
                 (map (fn [{:keys [identifier scheme]}]
                        [(keyword scheme) identifier]))
                 (into {}))]
    (assoc person :wikidata-q (get ids :wikidata))))

(defn expand [person]
  (assoc person :birth-place (wikidata/birth-place (:wikidata-q person))))

(defn prune [person]
  (dissoc
    person
    :image :images :other_names :links :contact_details
    :family_name :sort_name :identifiers :given_name))

(defn parse-area-id [area-id]
  (let [lookup (->> (string/split area-id #"\/")
                    (map #(string/split % #":"))
                    (take-last 2)
                    (into {}))]
    (format "%s-%s"
            (string/upper-case (get lookup "state"))
            (or (get lookup "cd")
                "0"))))

(defn memberships-for [term memberships]
  (let [all (-> (group-by :legislative_period_id memberships)
                (get term))]
    (->> all
         (map #(assoc % :slug (parse-area-id (:area_id %))))
         (map (fn [m] [(:slug m) (:person_id m)]))
         (into {}))))

(defn raw-congress []
  (-> (slurp "src/notebook/congress/congress_114.json")
      (json/read-str :key-fn keyword)
      (into {})))

(defn as-points [people districts]
  (->> people
       (map (fn [[slug person]]
              (assoc person :slug slug)))
       (remove (fn [{:keys [birth-place]}]
                 ;(println (first (second birth-place)))
                 (nil? (first (second birth-place)))))
       (map (fn [{:keys [birth-place slug] :as p}]
              (let [color (colors/random)
                    district (get districts slug)
                    ;district-center (districts/get-district-center district)
                    ]
                (if district
                  [(geojson/make
                     "Point"
                     (second birth-place)
                     {:name (format "(%s) %s" (:slug p) (:name p))
                      :marker-color color
                      :marker-size "small"
                      :marker-symbol "hospital"}
                     {})
                   #_(geojson/make
                       "Point"
                       district-center
                       {:name (format "%s" (:slug p))
                        :marker-color color
                        :marker-size "small"
                        :marker-symbol "campsite"}
                       {})
                   #_(geojson/make
                       "LineString"
                       [(second birth-place)
                        district-center]
                       {:stroke color
                        :stroke-width 2
                        :stroke-opacity 1.0}
                       {})
                   (update-in
                     district
                     [:properties]
                     (fn [props]
                       (merge props
                              {:fill color
                               :fill-opacity 0.25})))]
                  (do
                    (println p)
                    nil)))))
       ;(take 2)
       (remove nil?)
       (geojson/collect)))

(defn read-in [term state-filter]
  #_(:posts :persons :organizations :meta :memberships :events :areas)
  (let [raw (raw-congress)
        district-lookup (memberships-for term (:memberships raw))
        member-lookup (->> (:persons raw)
                           (map mapify)
                           ;(map expand)
                           (map prune)
                           (map (fn [{:keys [id] :as p}]
                                  [id p]))
                           (into {}))]
    (->> district-lookup
         (map (fn [[slug pid]]
                [slug (get member-lookup pid)]))
         (remove #(nil? (second %)))
         (filter #(re-find state-filter (first %)))
         (map (fn [[slug person]]
                [slug (expand person)])))))

(defn save-state [districts state]
  (let [data (-> (read-in "term/114" (re-pattern (str "^" state)))
                 (as-points districts)
                 (json/write-str))]
    (spit (format "tmp/__%s.json" (string/lower-case state))
          data)))