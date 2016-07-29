(ns notebook.congress
  (:require [clojure.data.json :as json]
            [notebook.wikidata :as wikidata]
            [clojure.string :as string]))

(defn mapify [person]
  (-> person
      (update :identifiers
              (fn [ids]
                (->> ids
                     (map (fn [{:keys [identifier scheme]}]
                            [(keyword scheme) identifier]))
                     (into {}))))))

(defn expand [person]
  (let [wikidata-q (get-in person [:identifiers :wikidata])]
    (assoc person :birth-place (wikidata/birth-place wikidata-q))))

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

(defn read-in [term]
  #_(:posts :persons :organizations :meta :memberships :events :areas)
  (let [raw (raw-congress)
        district-lookup (memberships-for term (:memberships raw))
        member-lookup (->> (:persons raw)
                           (map mapify)
                           (map expand)
                           (map prune)
                           (map (fn [{:keys [id] :as p}]
                                  [id p]))
                           (into {}))]
    (->> district-lookup
         (map (fn [[slug pid]]
                [slug (get member-lookup pid)])))
    ;district-lookup
    ))
