(ns notebook.spotify
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clj-spotify.core :as spotify]
            [clojure.data.codec.base64 :as b64]))

(def enc-auth-string
  (str
    "Basic "
    (-> (str "2e38d02c465244948f698721df2f5121"
             ":" "af7c1188b62545149b283b1b61b7e134")
        (.getBytes)
        (b64/encode)
        (String. "UTF-8"))))

(defonce
  spotify-oauth-token
  (->
    "https://accounts.spotify.com/api/token"
    (client/post
      {:form-params {:grant_type "client_credentials"}
       :headers {:Authorization enc-auth-string}})
    :body
    (json/read-str :key-fn keyword)
    :access_token))

(defn get-json [url]
  (-> (client/get url {:oauth-token spotify-oauth-token})
      (:body)
      (json/read-str :key-fn keyword)))

(defn get-all-tracks
  ([url]
    (get-all-tracks url []))
  ([url tracks]
   (let [more-tracks (get-json url)
         all-tracks (concat tracks (:items more-tracks))]
     (if-let [next-url (:next more-tracks)]
       (get-all-tracks next-url all-tracks)
       (map :track all-tracks)))))

(defn playlist-url [who which]
  (format "https://api.spotify.com/v1/users/%s/playlists/%s/tracks"
          who
          which))

#_(def countoffs-url
  (playlist-url "robstenson" "2GBrd2LhuWj8CwioluMJ9L"))

#_(def land-of-1000-url
  (playlist-url "robstenson" "2rI5OJBLx9ZD2vSb1iDw4C"))

#_(defonce loatd
  (->> (get-all-tracks countoffs-url [])
       (filter #(re-find #"^Land [oO]f" (:name %)))))

#_(defn filter-name [re]
  (filter #(re-find re (:name %)) _1234))