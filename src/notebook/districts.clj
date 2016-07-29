(ns notebook.districts
  (:require [clojure.data.json :as json]
            [geo.jts :as jts]
            [notebook.congress.about :as about]))

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
  (-> (slurp "src/notebook/congress/geo.json")
      (json/read-str :key-fn keyword)
      (:features)))

(defn get-districts [features]
  (simplify-features features))