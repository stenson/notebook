(ns notebook.zs
  (:require [clojure.string :as string]
            [puget.printer :refer [cprint]]
            [notebook.gdoc :as gdoc]
            [hiccup.page :refer [html5]]
            [notebook.css :refer [ß ß+]]
            [notebook.html :as html]
            [notebook.hiero :refer [<-txt as-txt]]))

;(def map "http://www.bigmapblog.com/2013/birdseye-view-of-los-angeles/")

; Notable Events of June 25
; Recording of Sundays at the Village Vanguard
; Birthdays
; Deathdays
; International year of the pulse (http://www.fao.org/pulses-2016/en/)

(def site "zhengstenson.com")

(defn txt [f]
  (->> (format "sites/%s/txt/%s.txt" site (name f))
       (slurp)
       (<-txt)))

(defn q [question answer]
  [:div.q
   [:h3.question question]
   [:p.answer answer]])

(def mapgl true)

(def pages
  [{:slug "about"
    :title "Diana & Rob"
    :html [:div#text-inner (txt :essay)]}
   {:slug "details"
    :title "Details"
    :css [(if mapgl
            "https://api.tiles.mapbox.com/mapbox-gl-js/v0.13.1/mapbox-gl"
            "https://api.mapbox.com/mapbox.js/v2.3.0/mapbox")]
    :js [(if mapgl
           "https://api.tiles.mapbox.com/mapbox-gl-js/v0.13.1/mapbox-gl"
           "https://api.mapbox.com/mapbox.js/v2.3.0/mapbox")
         (if mapgl
           "/map"
           "/map-classic")]
    :html [:div
           [:div#map]
           [:div#text-inner
            [:div#details
             (q "When & Where is the Rehearsal Dinner?"
                [:ul
                 [:li [:span "When"] "June 25th, 2016"]
                 [:li [:span "When"] "?"]
                 [:li [:span "Where"] "The Fig House"]
                 [:li [:span "Where"] "6433 N Figueroa St Los Angeles, CA 90042"]])
             (q "When & Where is the Wedding?"
                [:ul
                 [:li [:span "When"] "June 26th, 2016"]
                 [:li [:span "When"] "5pm - Midnight"]
                 [:li [:span "Where"] "The Carondelet House"]
                 [:li [:span "Where"] "627 S Carondelet St Los Angeles, CA 90057"]])]]]}
   {:slug "registry"
    :title "Registry"
    :html [:div#text-inner
           [:h3 "Registry"]
           [:p "(tbd)"]]}])

(defn page
  ([html]
    (page nil html))
  ([{:keys [slug title js css]} html]
   (html/refresh
     (str "zhengstenson.com" (when slug (str "/" slug)))
     (str "Zheng & Stenson Wedding" (when title (str " — " title)))
     {:styles (concat css ["/klim" "/style"])
      :scripts (concat ["/hyphenator"] js)}
     [:div#container
      [:div#header-container
       [:div#header
        [:a#home {:href "/"}
         [:img#logo {:src "/sz-circle.png" :alt "Zheng & Stenson"}]
         [:h3 "Zheng & Stenson"]]]]
      [:div#navigation-container
       [:div#navigation
        (for [[title slug]
              [["Diana & Rob" "about"]
               ["Details" "details"]
               ["Registry" "registry"]]]
          [:div.nav-item-container
           [:a.nav-item {:href (str "/" slug)}
            [:span title]]])]]
      (if html
        [:div#content-outer
         [:div#content
          html]])])))

(do
  (page nil)
  (doall
    (for [p pages]
      (page p (:html p)))))

#_(html/refresh
  site
  "Zheng Stenson Wedding"
  {:styles ["klim" "style"]
   :scripts ["hyphenator"]}
  [:div#container
   [:div#text-outer
    [:div#text-inner
     [:h1 "Diana &amp; Rob"]
     [:div#dh
      [:a {:href "https://en.wikipedia.org/wiki/Double_Happiness_(calligraphy)"
           :target "_blank"}
       [:img {:src "dh.png"}]]]
     [:img.sz {:src "sz-512.png" :width 32}]
     [:div.content (txt :essay)]
     [:div.details (txt :details)]]]])