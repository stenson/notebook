(ns notebook.http
  (:require [clj-http.client :as client]
            [cemerick.url :as url]
            [clojure.data.json :as json]
            [rodeo.core :as rodeo]))

(defn fetch
  ([url params]
   (fetch false url params))
  ([json? url params]
   (let [full-url (str url "?" (url/map->query params))
         cache (str "tmp/cache/__geonames-" (hash full-url))]
     (try
       (read-string (slurp cache))
       (catch Exception _
         (println "GET: " full-url)
         (let [res (:body (client/get full-url))
               data (if json?
                      (json/read-str res :key-fn keyword)
                      res)]
           (spit cache (with-out-str (pr data)))
           data))))))

(defn fetch-json [url params]
  (fetch true url params))

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