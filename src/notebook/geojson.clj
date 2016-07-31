(ns notebook.geojson)

(defn make [geometry-type coordinates properties style]
  {:type "Feature"
   :properties (merge properties style)
   :geometry {:type geometry-type
              :coordinates coordinates}})

(defn collect [features]
  {:type "FeatureCollection"
   :features (flatten features)})