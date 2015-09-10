(ns notebook.scrape
  (:require [net.cgrand.enlive-html :as html]
            [cemerick.url :as url]
            [clojure.java.io :refer [as-url]]
            [puget.printer :refer [cprint]]
            [clojure.string :as string]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [me.raynes.fs :as fs]
            [clojure.pprint :refer [pprint]]))

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
   (println "GET" title)
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
    {:title   (:title content)
     :content (string/split-lines (or (:* (first (:revisions content))) ""))}))

(defn- extract-infobox [lines]
  (->> (filter #(re-find #"^\|" %) lines)
       (map (fn [line]
              (let [xs (map string/trim (string/split line #"="))]
                [(keyword (string/replace (first xs) #"^\|([\s]+)?" ""))
                 (string/join "=" (rest xs))])))
       (into {})))

(defn- extract-list [start end [line & lines] out-lines]
  (if (nil? out-lines)
    (if (re-find start line)
      (extract-list start end lines [])
      (extract-list start end lines nil))
    (if (or (nil? line) (re-find end line))
      out-lines
      (extract-list start end lines (vec (conj out-lines line))))))

(def ^:private redirect-re #"^\#REDIRECT(\s+)?\[\[([^\]]+)\]\]")

(defn wikipedia-json [title]
  (let [res (wikipedia-get title)]
    (if (= 200 (:status res))
      (let [article (wikipedia-content res)]
        (if-let [rd (re-find redirect-re (first (:content article)))]
          (wikipedia-json (last rd))
          article)))))

(defn- unbracket [s]
  (string/replace s #"^[\{\[]+|[\}\]]+$" ""))

(defn- format-cols [cols sep s]
  (let [ss (map string/trim (string/split s sep))
        paired (if (sequential? (first cols))
                 (->> (map vector (map first cols) ss (map second cols))
                      (map (fn [[k v f]] [k ((or f identity) v)])))
                 (map vector cols ss))]
    (into {} paired)))

(defn- str->int [s]
  (try
    (Integer/parseInt (re-find #"[0-9]+" s))
    (catch Exception _ 0)))

(defn- parse-district [s]
  (let [{:keys [state number]}
        (format-cols [:tag :state :number :str] #"\|" (unbracket s))]
    [state (str->int number)]))

(defn- parse-name [s]
  (let [cols (format-cols [:tag :first :last :full] #"\|" (unbracket s))]
    (or (:full cols)
        (str (:first cols) " " (:last cols)))))

(defn- parse-link [s]
  (-> (unbracket s)
      (string/split #"\|")
      (first)))

(defn- split-breaks [s]
  (->> (string/split s #"\<br([\s\/]+)?\>")
       (map string/trim)))

(defn- extract-links [s]
  (->> (re-seq #"\[\[([^\|\]]+)([^\]]+)?\]\]" (or s ""))
       (map (fn [[_ l _]] l))))

(defn- extract-first-link [s]
  (first (extract-links (or s ""))))

(def ushr "Current_members_of_the_United_States_House_of_Representatives")

(defn summarize-congressman [title]
  (println "SUMMARIZE" title)
  (let [info (extract-infobox (:content (wikipedia-json title)))]
    {:birth-place (first (extract-links (:birth_place info)))
     :alma-maters (map extract-first-link (split-breaks (or (:alma_mater info) "")))}))

(def ^:private ushr-columns
  [[:style]
   [:district parse-district]
   [:portrait]
   [:sortname parse-name]
   [:party]
   [:religion parse-link]
   [:experience]
   [:schools #(map extract-first-link (split-breaks %))]
   [:started str->int]
   [:born str->int]])

(defn extract-congresspeople [content]
  (->> (extract-list #"^==Voting" #"^==" content nil)
       (filter #(re-find #"^\|" %))
       (remove #(re-matches #"\|\-" %))
       (map (partial format-cols ushr-columns #"\|\|"))
       (map #(select-keys % [:sortname :district :started :schools]))
       (remove empty?)
       (remove #(string/blank? (:sortname %)))
       (map (fn [{:keys [district] :as c}]
              (assoc c :state (first district)
                       :district (second district))))))

(defn get-ushr []
  (->> (wikipedia-json ushr)
       (:content)
       (extract-congresspeople)))

(defn add-full-bio [cp]
  (merge cp (summarize-congressman (:sortname cp))))

(defn write-full-congresspeople []
  (fs/mkdir "tmp")
  (->> (get-ushr)
       (map-indexed
         (fn [i {:keys [district state] :as cp}]
           (println i)
           (let [full (add-full-bio cp)]
             (spit (format "tmp/%s-%s" state district)
                   (with-out-str (pprint full)))
             full)))))

(defn read-local-ushr []
  (map read-string (map slurp (fs/list-dir "tmp/"))))

(defn collect-places [xs]
  (->> (map #(select-keys % [:birth-place :alma-maters :schools]) xs)
       (map vals)
       (flatten)
       (remove nil?)
       (into #{})))