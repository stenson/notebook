(ns notebook.congress.directory
  (:require [clojure.data.json :as json]
            [notebook.wikidata :as wikidata]
            [notebook.congress :as congress]
            [clojure.string :as string]
            [notebook.congress.about :as about]
            [notebook.districts :as districts]
            [notebook.geojson :as geojson]
            [notebook.colors :as colors]
            [notebook.states :as states]
            [me.raynes.fs :as fs]
            [geo.jts :as jts]
            [geo.spatial :as spatial]
            [notebook.http :as http]
            [clj-http.client :as client]))

(def all "http://memberguide.gpo.gov/Congressional.svc/GetMembers/114")
(def bio-fmt "http://memberguide.gpo.gov/Congressional.svc/GetMember/%s")

(defn geonames [q]
  (let [body (http/fetch-json
               "http://api.geonames.org/search"
               {:q q :username "robstenson" :type "json" :maxRows 1})
        ll (-> (:geonames body)
               (first)
               (select-keys [:lat :lng])
               (vals)
               (reverse))]
    (map #(Float/parseFloat %) ll)))

(def members
  (json/read-str (slurp "tmp/_memberguide-114.json") :key-fn keyword))

(def representatives
  (filter #(= "RP" (:MemberTypeId %)) members))

(def senators
  (filter #(= "SR" (:MemberTypeId %)) members))

(defn fetch-bio [member]
  (let [mid (:MemberId member)
        body (:body (client/get (format bio-fmt mid)))]
    (spit (format "tmp/_member-%s.json" mid) body)))

(defn get-slug [{:keys [StateId District]}]
  (str StateId "-" (if (= "At Large" District)
                     "0"
                     District)))

(defn get-bio [member]
  (-> (slurp (format "tmp/_member-%s.json" (:MemberId member)))
      (json/read-str :key-fn keyword)
      (assoc :slug (get-slug member))))

(defn attempt-city-geocode [where who]
  (let [state-abbrv (last (string/split where #", "))
        where-mod (if (get about/state-abbrvs state-abbrv)
                    (str where ", USA")
                    where)]
    (let [out (geonames where-mod)]
      (if (empty? out)
        (do
          (println "N/A: " (:Name who) where-mod)
          nil)
        out))))

(defn simplify-places [bio]
  (let [representative? (= "RP" (:MemberTypeId bio))
        state-id (:StateId bio)
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
                            (format "%s, %s %s %s" Street City state-id Zip))))
        info {:offices (map vector offices (http/geocode offices))
              :hometown [hometown
                         (attempt-city-geocode hometown bio)]
              :birth-place [birth-place
                            (attempt-city-geocode birth-place bio)]}]
    (if representative?
      (assoc info :district (get (districts/district-lookup nil) (:slug bio)))
      (assoc info :state (get states/states state-id)))))

(defn geo-places [{:keys [offices hometown birth-place district]}]
  (let [to-coord (fn [place]
                   (let [pt (apply
                              spatial/spatial4j-point
                              (reverse (second place)))]
                     (spatial/circle pt 5000)))]
    {:offices (map to-coord offices)
     :hometown (to-coord hometown)
     :birth-place (to-coord birth-place)
     :district (geojson/to-shape district)}))

(defn get-by-district [slug]
  (->> members
       (filter #(= "RP" (:MemberTypeId %)))
       (filter #(= slug (get-slug %)))
       (first)
       (get-bio)))

(defn get-by-state [abbrv]
  (->> members
       (filter #(and (= abbrv (:StateId %)) (= "RP" (:MemberTypeId %))))))

(defn slug-or-member->bio [s-or-m]
  (if (string? s-or-m)
    (get-by-district s-or-m)
    (get-bio s-or-m)))

(defn calc-stats [slug-or-member]
  (let [inside? (fn [a b]
                  (let [relation (spatial/relate a b)]
                    (or (= :contains relation)
                        (= :intersects relation))))
        bio (slug-or-member->bio slug-or-member)]
    (println (:slug bio))
    (let [places (simplify-places bio)
          state (get states/states (:StateId bio))
          {:keys [offices birth-place hometown district]} (geo-places places)]
      {:birth-place (first (:birth-place places))
       :hometown (first (:hometown places))
       :name (string/trim (:Name bio))
       :slug (:slug bio)
       :party (:PartyId bio)
       :same-exact-place (= (first (:hometown places))
                            (first (:birth-place places)))
       :born-in-state (inside? (:shape state) birth-place)
       :born-in-us (inside? states/nation birth-place)
       :born-there (inside? district birth-place)
       :lives-there (inside? district hometown)
       :distance (Math/round
                   (float
                     (* (spatial/distance
                          (spatial/center birth-place)
                          (spatial/center district))
                        0.000621371)))
       :offices-out-of-district (->> (map (partial inside? district) offices)
                                     (some false?)
                                     (boolean))})))

(defn as-features [{:keys [offices hometown birth-place district]} bio]
  (let [color (colors/random)
        slug (:slug bio)]
    (geojson/collect
      (drop
        3
        [(map (fn [[address ll]]
                (geojson/make
                  "Point"
                  ll
                  {:address address
                   :type "office"
                   :slug slug}
                  {:marker-color color
                   :marker-size "small"}))
              offices)
         (geojson/make
           "Point"
           (second hometown)
           {:name (first hometown)
            :type "hometown"
            :slug slug}
           {:marker-color color
            :marker-size "medium"})
         (geojson/make
           "Point"
           (second birth-place)
           {:name (first birth-place)
            :type "birth-place"}
           {:marker-color color
            :marker-size "large"
            :slug slug})
         (update-in
           district
           [:properties]
           (fn [props]
             (merge props
                    (assoc (calc-stats bio)
                      :gpo (dissoc bio :OfficeList :SocialMediaList))
                    {:fill color
                     :fill-opacity 0.5})))]))))

(defn save-geo-bio [slug-or-member]
  (let [bio (slug-or-member->bio slug-or-member)
        places (simplify-places bio)
        path (format "tmp/__district--%s.json" (:slug bio))
        features (as-features places bio)]
    (spit path (json/write-str features))
    features))

(defn combine-geo-bios [matcher]
  (->> (fs/list-dir "tmp")
       (filter #(re-find matcher (fs/base-name %)))
       (map slurp)
       (map #(json/read-str % :key-fn keyword))
       (map :features)
       (apply concat)
       (geojson/collect)
       (json/write-str)))

(defn save-all-geo-bios []
  (let [features (->> representatives
                      (map save-geo-bio)
                      (map :features)
                      (apply concat)
                      (geojson/collect))]
    (spit "sites/robstenson.com/articles/birthplaces/members.json"
          (json/write-str features))))

(defn state-stats [state]
  (let [stats (->> (get-by-state state)
                   (map calc-stats))
        grouped (group-by :party stats)
        r (get grouped "R")
        d (get grouped "D")
        r-count (count r)
        d-count (count d)
        get-percent (fn [which from]
                      (let [what (map which from)
                            trues (count (filter true? what))]
                        (int (* 100 (float (/ trues (count what)))))))
        get-percents (fn [which]
                       [(get-percent which stats)
                        (get-percent which r)
                        (get-percent which d)])]
    [[:counts [:r r-count :d d-count]]
     [:born-in-state (get-percents :born-in-state)]
     [:born-in-us (get-percents :born-in-us)]
     [:born-there (get-percents :born-there)]
     [:same-exact-place (get-percents :same-exact-place)]]))

; more often than not, birthplace tells a story
; the military families of republicans
; the internment of doris matsui

; what state has imported the most reps?
; what state has exported the most reps?