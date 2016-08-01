(ns notebook.geojson
  (:require [geo.jts :as jts]
            [geo.spatial :as spatial]))

(defn make [geometry-type coordinates properties style]
  {:type "Feature"
   :properties (merge properties style)
   :geometry {:type geometry-type
              :coordinates coordinates}})

(defn collect [features]
  {:type "FeatureCollection"
   :features (flatten features)})

(defn to-shape [f]
  (let [{:keys [coordinates type]} (get f :geometry)
        to-poly (fn [cs]
                  (->> cs
                       (map #(apply jts/coordinate %))
                       (jts/linear-ring)
                       (jts/polygon)))]
    (case type
      "Point" (apply spatial/spatial4j-point coordinates)
      "Polygon" (to-poly (first coordinates))
      "MultiPolygon" (->> coordinates
                          (map #(to-poly (first %)))
                          (jts/multi-polygon)))))