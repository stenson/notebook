(ns notebook.congress.directory
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [notebook.wikidata :as wikidata]
            [notebook.congress :as congress]
            [rodeo.core :as rodeo]
            [clojure.string :as string]
            [notebook.congress.about :as about]
            [notebook.districts :as districts]
            [notebook.geojson :as geojson]
            [notebook.colors :as colors]
            [me.raynes.fs :as fs]
            [geo.jts :as jts]
            [geo.spatial :as spatial]
            [cemerick.url :as url]))

(def all "http://memberguide.gpo.gov/Congressional.svc/GetMembers/114")
(def bio-fmt "http://memberguide.gpo.gov/Congressional.svc/GetMember/%s")

(defn geonames [q]
  (let [body (->> (url/map->query {:q q :username "robstenson" :type "json" :maxRows 1})
                  (format "http://api.geonames.org/search?%s")
                  (client/get)
                  (:body))
        ll (-> (json/read-str body :key-fn keyword)
               (:geonames)
               (first)
               (select-keys [:lat :lng])
               (vals)
               (reverse))]
    (map #(Float/parseFloat %) ll)))

(def geocodio-api "8f995738c589587cc7fa9f97aff9d995971f550")

(defn geocode [where]
  (let [h (hash where)
        f (format "tmp/_geocodio_%s" h)]
    (try
      (read-string (slurp f))
      (catch Exception _
        (let [res (->> (rodeo/batch where :api-key geocodio-api)
                       (:results)
                       (map #(:location (first (:results (:response %)))))
                       (map
                         (fn [{:keys [lat lng]}]
                           [lng lat])))]
          (spit f (with-out-str (pr res)))
          res)))))

(def members
  (json/read-str (slurp "tmp/_memberguide-114.json") :key-fn keyword))

(defn fetch-bio [member]
  (let [mid (:MemberId member)
        body (:body (client/get (format bio-fmt mid)))]
    (spit (format "tmp/_member-%s.json" mid) body)))

(defn get-bio [member]
  (-> (slurp (format "tmp/_member-%s.json" (:MemberId member)))
      (json/read-str :key-fn keyword)
      (assoc :slug (str (:StateId member) "-" (:District member)))))

(defn attempt-city-geocode [where]
  #_(let [[city state] (string/split where #",")
        long-state (get about/state-abbrvs (string/trim state))
        long (str city ", " long-state)]
    (if-let [ll (wikidata/geocode where)]
      ll
      (if long-state
        (if-let [ll (wikidata/geocode long)]
          ll
          (let [ll (first (geocode where))]
            (if (and (first ll) (second ll))
              ll
              (if-let [ll (wikidata/geocode city)]
                ll))))
        (if-let [ll (wikidata/geocode city)]
          ll))))
  (geonames where))

(defn as-features [{:keys [offices hometown birth-place district]} bio]
  (let [color (colors/random)]
    (geojson/collect
      [(map (fn [[address ll]]
              (geojson/make
                "Point"
                ll
                {:name address}
                {:marker-color color
                 :marker-size "medium"
                 :marker-symbol "star-stroked"}))
            offices)
       (geojson/make
         "Point"
         (second hometown)
         {:name (first hometown)}
         {:marker-color color})
       (geojson/make
         "Point"
         (second birth-place)
         {:name (first birth-place)}
         {:marker-color color
          :marker-size "medium"
          :marker-symbol "hospital"})
       (update-in
         district
         [:properties]
         (fn [props]
           (merge props
                  bio
                  {:fill color
                   :fill-opacity 0.5})))])))

(defn simplify-places [bio]
  (let [state-id (:StateId bio)
        state (:StateDescription bio)
        hometown (format "%s, %s" (:Hometown bio) state-id)
        birth-place (let [bp (:BirthPlace bio)
                          [city state] (string/split bp #",")]
                      (if state
                        bp
                        (str city ", " state-id)))
        offices (->> (:OfficeList bio)
                     (filter #(= state (:StateDescription %)))
                     (map (fn [{:keys [Street City Zip]}]
                            (format "%s, %s %s %s" Street City state-id Zip))))]
    {:offices (map vector offices (geocode offices))
     :hometown [hometown
                (attempt-city-geocode hometown)]
     :birth-place [birth-place
                   (attempt-city-geocode birth-place)]
     :district (get (districts/district-lookup nil) (:slug bio))}))

(defn geo-places [{:keys [offices hometown birth-place district]}]
  (let [to-coord (fn [place]
                   (let [pt (apply
                              spatial/spatial4j-point
                              (reverse (second place)))]
                     (spatial/circle pt 5000)))]
    {:offices (map to-coord offices)
     :hometown (to-coord hometown)
     :birth-place (to-coord birth-place)
     :district (->> (first (get-in district [:geometry :coordinates]))
                    (map #(apply jts/coordinate %))
                    (jts/linear-ring)
                    (jts/polygon))}))

(defn get-by-district [slug]
  (->> members
       (filter #(= "RP" (:MemberTypeId %)))
       (filter #(= slug (str (:StateId %) "-" (:District %))))
       (first)
       (get-bio)))

(defn get-by-state [abbrv]
  (->> members
       (filter #(and (= abbrv (:StateId %)) (= "RP" (:MemberTypeId %))))))

(defn slug-or-member->bio [s-or-m]
  (if (string? s-or-m)
    (get-by-district s-or-m)
    (get-bio s-or-m)))

(defn save-geo-bio [slug-or-member]
  (let [bio (slug-or-member->bio slug-or-member)
        places (simplify-places bio)
        path (format "tmp/__district--%s.json" (:slug bio))]
    [(dissoc places :district)
     (->> (as-features places bio)
          (json/write-str)
          (spit path))
     path]))

(defn combine-geo-bios [matcher]
  (->> (fs/list-dir "tmp")
       (filter #(re-find matcher (fs/base-name %)))
       (map slurp)
       (map #(json/read-str % :key-fn keyword))
       (map :features)
       (apply concat)
       (geojson/collect)
       (json/write-str)))

(defn calc-stats [slug-or-member]
  (let [inside? (fn [a b]
                  (let [relation (spatial/relate a b)]
                    (or (= :contains relation)
                        (= :intersects relation))))
        bio (slug-or-member->bio slug-or-member)
        places (simplify-places bio)
        {:keys [offices birth-place hometown district]} (geo-places places)]
    {:birth-place (first (:birth-place places))
     :hometown (first (:hometown places))
     :name (string/trim (:Name bio))
     :slug (:slug bio)
     :party (:PartyId bio)
     :born-there (inside? district birth-place)
     :lives-there (inside? district hometown)
     :offices (map (partial inside? district) offices)}))