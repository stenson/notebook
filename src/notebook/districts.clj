(ns notebook.districts
  (:require [clojure.data.json :as json]
            [geo.jts :as jts]
            [notebook.congress.about :as about]
            [geo.spatial :as spatial]
            [notebook.geojson :as geojson])
  (:import (com.vividsolutions.jts.simplify TopologyPreservingSimplifier)))
;http://www.vividsolutions.com/jts/javadoc/com/vividsolutions/jts/simplify/TopologyPreservingSimplifier.html

(defn to-i [s]
  (try
    (Integer/parseInt s)
    (catch Exception _
      s)))

(defn feature->polygon [feature]
  (let [fs (first (get-in feature [:geometry :coordinates]))]
    (-> (map (fn [[x y]] (jts/coordinate x y)) fs)
        (jts/linear-ring)
        (jts/polygon))))

(defn feature->simplified [{:keys [properties] :as feature}]
  (let [state (:STATE properties)]
    (when state
      (let [key (format "%s-%s" state (Integer/parseInt (:CONG_DIST properties)))]
        [key
         feature]))))

(defn simplify-features [features]
  (->> (map feature->simplified features)
       (remove nil?)
       (group-by first)
       (map
         (fn [[k vs]]
           (let [polys (map second vs)]
             [k
              (cond
                (= num 0) nil
                (= num 1) (first polys)
                :else (let [[fst & rst] polys]
                        (-> fst
                            (assoc-in [:geometry :type] "MultiPolygon")
                            (assoc-in
                              [:geometry :coordinates]
                              (->> polys
                                   (map #(get-in % [:geometry :coordinates])))))))])))
       (into {})))

(defn get-district-features []
  (-> (slurp "src/notebook/congress/districts_114.json")
      (json/read-str :key-fn keyword)
      (:features)))

(defn coords->polygon [coords]
  (-> (map (fn [[x y]] (jts/coordinate x y)) (first coords))
      (jts/linear-ring)
      (jts/polygon)))

(defn get-district-center [district]
  (when-let [type (get-in district [:geometry :type])]
    (let [coords (get-in district [:geometry :coordinates])
          shape (case type
                  "MultiPolygon" (->> (map coords->polygon coords)
                                      (jts/multi-polygon))
                  "Polygon" (coords->polygon coords))
          center (spatial/center shape)]
      [(spatial/longitude center)
       (spatial/latitude center)])))

(def all-districts (get-district-features))

(defn district-lookup [districts?]
  (->> (or districts? all-districts)
       (map (fn [district]
              (let [ps (:properties district)
                    district-num (to-i (:CD114FP ps))
                    state-fp-num (to-i (:STATEFP ps))
                    [state-abbrv state-name] (get about/fips state-fp-num)
                    slug (str state-abbrv "-" district-num)]
                (when (and (not= "ZZ" district-num)
                           (not= 98 district-num))
                  [slug
                   (-> district
                       (assoc :properties
                              {:slug slug
                               :district district-num
                               :state state-abbrv
                               :fips (get about/reverse-fips state-abbrv)
                               :name (format "%s, %s"
                                             state-name
                                             (:NAMELSAD ps))})
                       ;(dissoc :geometry)
                       )]))))
       (remove nil?)
       (into {})))

(defn get-districts []
  (simplify-features (get-district-features)))

; does state match?
; how far from current office -> http://memberguide.gpo.gov/
; possible to check school?
; how far?