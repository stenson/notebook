(ns notebook.zs
  (:require [clojure.string :as string]
            [puget.printer :refer [cprint]]
            [notebook.gdoc :as gdoc]
            [hiccup.core :as html]
            [hiccup.page :refer [html5]]
            [garden.core :as garden]
            [garden.color :as color]))

(def essay "14ANhea-S4bz9GOOOPIwCqfhKyGFpWQ2oQHtW1K-kHuQ")

(defn save-essay []
  (let [html (gdoc/as-hiero-html essay)]
    (spit "wedding.html" (:html html))))

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

(let [;content (slurp "wedding.html")
      content (:html (gdoc/as-hiero-html essay))]
  (spit
    "zhengstenson.com/index.html"
    (html5
      {:lang "en"}
      (list
        [:head
         [:meta {:charset "utf-8"}]
         [:title "Zheng Stenson Wedding"]
         (list (favicons 16 32 96))
         [:link {:type "text/css" :href "klim.css" :rel "stylesheet"}]
         [:link {:type "text/css" :href "style.css" :rel "stylesheet"}]]
        [:body
         [:div#container
          [:div#text-outer
           [:div#text-inner
            [:h1 "Diana &amp; Rob"]
            [:img.sz {:src "sz-512.png" :width 32}]
            [:div.content
             content]]]]
         [:script {:type "text/javascript"
                   :src "hyphenator.js"}]]))))