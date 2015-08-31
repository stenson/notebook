(ns notebook.scrape
  (:require [net.cgrand.enlive-html :as html]
            [cemerick.url :as url]
            [clojure.java.io :refer [as-url]]
            [puget.printer :refer [cprint]]
            [clojure.string :as string]
            [clj-http.client :as client]
            [clojure.data.json :as json]))

(defn fetch [url-str]
  (html/html-resource (as-url url-str)))

(defn wikipedia [title]
  (format "https://en.wikipedia.org/wiki/%s" (url/url-encode title)))

(defn wikipedia-infobox [title]
  (-> (fetch (wikipedia title))
      (html/select [:table.infobox :tr])))

(defn where-born? [person]
  (->> (wikipedia-infobox (string/replace person #"\s" "_"))
       (filter #(re-find #"Born" (html/text %)))
       (map #(html/select % [:td :a]))
       (flatten)
       (map (fn [a]
              {:href (string/replace (:href (:attrs a)) #"^\/wiki\/" "")
               :text (html/text a)}))))

(def wiki-cache (atom {}))

(defn- wikipedia-get-uncached
  ([title]
    (wikipedia-get-uncached title nil))
  ([title options]
   (->> {:action "query"
         :prop   "revisions"
         :rvprop "content"
         :format (get options :format "json")
         :titles title}
        (url/map->query)
        (format "https://en.wikipedia.org/w/api.php?%s")
        (client/get))))

(defn- wikipedia-get [title]
  (if-let [got (get @wiki-cache title)]
    (assoc got :notebook-cached true)
    (let [got (wikipedia-get-uncached title)]
      (swap! wiki-cache assoc title got)
      got)))

(defn- wikipedia-content [res]
  (let [content (-> (get res :body)
                    (json/read-str :key-fn keyword)
                    (get :query)
                    (get :pages)
                    (first)
                    (second))]
    {:title (:title content)
     :content (string/split-lines
                (:* (first (:revisions content))))}))

(defn- extract-infobox [lines]
  (->> (filter #(re-find #"^\|" %) lines)
       (map (fn [line]
              (let [xs (map string/trim (string/split line #"="))]
                [(keyword (string/replace (first xs) #"^\|([\s]+)?" ""))
                 (string/join "=" (rest xs))])))
       (into {})))

(defn- extract-list [sentinel lines]
  (->> (take-while (partial = sentinel) lines)))

(defn wikipedia-json [title]
  (let [res (wikipedia-get title)]
    (if (= 200 (:status res))
      (wikipedia-content res))))

(def ushr "Current_members_of_the_United_States_House_of_Representatives")

(defn summarize-congressman [title]
  (let [info (extract-infobox (:content (wikipedia-json title)))]
    (select-keys info [:birth_place :alma_mater])))