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

(defn extract-infobox [lines]
  (let [info (->> (filter #(re-find #"^\|\s?[A-Za-z_]" %) lines)
                  (filter #(re-find #"\=" %))
                  (string/join "\n")
                  #_(map (fn [line]
                           (let [xs (map string/trim (string/split line #"="))]
                             [(keyword (string/replace (first xs) #"^\|([\s]+)?" ""))
                              (string/join "=" (rest xs))])))
                  #_(into {}))]
    (->> (string/split info #"\|")
         (map string/trim)
         (map #(string/split % #"="))
         (filter #(= (count %) 2))
         (map (fn [[k v]] [(keyword (string/trim k))
                           (string/trim v)]))
         (vec)
         (into {}))))

(defn extract-list [start end [line & lines] out-lines]
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

(defn degrees->decimal [d m s]
  (let [truncate #(.floatValue (with-precision 10 %))]
    (+ d (truncate (/ m 60.)) (truncate (/ s 3600)))))

(defn find-coords [lines]
  (when-let [coords (->> (map #(re-find #"\{\{([Cc]oord.*)\}\}" %) lines)
                         (remove nil?)
                         (first)
                         (second))]
    (if (re-find #"missing" coords)
      nil
      (if-let [llfs (re-seq #"[0-9]+\.[0-9]+" coords)]
        (map #(Float/parseFloat %) llfs)
        (let [[d1 m1 s1 _ d2 m2 s2] (->> (string/split coords #"\|")
                                         (drop 1)
                                         (take 8)
                                         (map str->int))]
          [(degrees->decimal d1 m1 s1)
           (degrees->decimal d2 m2 s2)])))))

(defn find-ld [which lines]
  ; needs to distinguish format float vs int
  (let [p (case which
            :lat #"\|(latd\s?=\s?.*)"
            :long #"\|(longd.*)")]
    (when-let [ld (->> (map #(re-find p %) lines)
                       (remove nil?)
                       (first)
                       (second))]
      (if-let [fl (re-find #"([0-9]+.[0-9]+)" ld)]
        (Float/parseFloat (second fl))
        (let [[d m s] (map #(Integer/parseInt %) (re-seq #"[0-9]+" ld))]
          (degrees->decimal d m s))))))

(defn get-ll-for-place [place]
  (let [wiki (:content (wikipedia-json place))
        {:keys [latd latm lats longd longm longs]} (extract-infobox wiki)]
    (if (and latd longd)
      [(if (and latd latm lats)
         (degrees->decimal (Integer/parseInt latd)
                           (Integer/parseInt latm)
                           (Integer/parseInt lats))
         (Float/parseFloat latd))
       (if (and longd longm longs)
         (degrees->decimal (Integer/parseInt longd)
                           (Integer/parseInt longm)
                           (Integer/parseInt longs))
         (Float/parseFloat longd))]
      (find-coords wiki))))

(defn get-coords-for-place [place]
  (let [wiki (:content (wikipedia-json place))]
    (if-let [coords (find-coords wiki)]
      coords
      (let [lat (find-ld :lat wiki)
            long (find-ld :long wiki)]
        (if (and lat long)
          [lat long]
          nil)))))

(defn collect-places [xs]
  (->> (map #(select-keys % [:birth-place #_:alma-maters #_:schools]) xs)
       (map vals)
       (flatten)
       (remove nil?)
       (into #{})))

;(collect-places (read-local-ushr))