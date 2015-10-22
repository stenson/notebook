(ns notebook.html
  (:require [garden.core :as garden]
            [hiccup.page :refer [html5]]))

(defn favicon [size]
  [:link {:href  (format "favicon-%sx%s.png" size size)
          :sizes (format "%sx%s" size size)
          :rel   "icon"
          :type  "image/png"}])

(defn favicons [& sizes]
  (->> (map favicon sizes)
       (conj [:link {:href "favicon.ico" :rel "icon" :type "image/x-icon"}])))

(defn inline-style [& styles]
  [:style {:type "text/css"}
   (garden/css {:pretty-print? false} styles)])

(defn style-link [href]
  [:link {:type "text/css"
          :href (format "%s.css" href)
          :rel "stylesheet"}])

(defn js-link [href]
  [:script {:type "text/javascript"
            :src  (format "%s.js" href)}])

(defn basic [title {:keys [styles scripts]} content]
  (html5
    {:lang "en"}
    (list
      [:head
       [:meta {:charset "utf-8"}]
       [:title title]
       (list (favicons 16 32 96))
       (list (map style-link styles))]
      [:body
       (list
         content
         (list (map js-link scripts)))])))

(defn refresh [site title options content]
  (spit
    (format "sites/%s/index.html" site)
    (basic title options content)))