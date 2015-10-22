(ns notebook.zs
  (:require [clojure.string :as string]
            [puget.printer :refer [cprint]]
            [notebook.gdoc :as gdoc]
            [hiccup.page :refer [html5]]
            [notebook.html :as html]))

(def essay "14ANhea-S4bz9GOOOPIwCqfhKyGFpWQ2oQHtW1K-kHuQ")

(html/refresh
  "zhengstenson.com"
  "Zheng Stenson Wedding"
  {:styles  ["klim" "style"]
   :scripts ["hyphenator"]}
  [:div#container
   [:div#text-outer
    [:div#text-inner
     [:h1 "Diana &amp; Rob"]
     [:img.sz {:src "sz-512.png" :width 32}]
     [:div.content
      (:html (gdoc/parse :html essay))]]]])