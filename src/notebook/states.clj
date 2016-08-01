(ns notebook.states
  (:require [clojure.data.json :as json]
            [notebook.geojson :as geojson]))

(defn get-geojson [f]
  (-> (slurp (format "src/notebook/congress/%s" f))
      (json/read-str :key-fn keyword)
      (:features)))

(defn get-states []
  (->> (get-geojson "states-named.geojson")
       (map (fn [{:keys [properties] :as f}]
              (when-let [s (:state properties)]
                [s {:geojson f
                    :shape (geojson/to-shape f)}])))
       (remove nil?)
       (into {})))

(defn get-nation []
  (geojson/to-shape (first (get-geojson "us.json"))))

(def states (get-states))
(def nation (get-nation))

