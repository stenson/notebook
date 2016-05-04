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
    :html
    [:div#text-inner
     [:div.text (txt :essay)]
     [:div.date "— September, 2015"]
     [:div.photos
      [:div.photo [:img {:src "/img/alf.jpg"}]]
      [:div.photo.tall [:img {:src "/img/bench.jpg"}]]
      [:div.photo.tall [:img {:src "/img/wallkiss.jpg"}]]
      [:div.photo [:img {:src "/img/legs.jpg"}]]
      [:div.photo [:img {:src "/img/guns.jpg"}]]
      [:div.photo.tall [:img {:src "/img/kiss.jpg"}]]
      [:div.photo.tall [:img {:src "/img/lift.jpg"}]]
      #_[:div.photo [:img {:src "/img/walking.jpg"}]]
      [:div.photo.tall [:img {:src "/img/wall.jpg"}]]
      [:div.photo.tall [:img {:src "/img/laugh.jpg"}]]
      [:div.photo [:img {:src "/img/front.jpg"}]]
      [:div.photo [:img {:src "/img/sunset.jpg"}]]
      [:div.photo [:img {:src "/img/city.jpg"}]]]]}
   {:slug "what-to-do-in-la"
    :title "What to do in Los Angeles"
    :html
    [:div#text-inner
     [:div.text (txt :la)]]}
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
                 [:li [:span "When"] "June 24th, 2016"]
                 [:li [:span "When"] "6pm – 10pm"]
                 [:li [:span "Where"] "The Fig House"]
                 [:li [:span "Where"] "6433 N Figueroa St Los Angeles, CA 90042"]
                 [:li.small "All are invited!; invitations will be mailed separately"]])
             (q "When & Where is the Wedding?"
                [:ul
                 [:li [:span "When"] "June 25th, 2016"]
                 [:li [:span "When"] "5:30pm – 11:00pm"]
                 [:li [:span "Where"] "The Carondelet House"]
                 [:li [:span "Where"] "627 S Carondelet St Los Angeles, CA 90057"]
                 [:li.small "Please arrive at least 15 minutes early to find your seat"]])
             (q "Where should I stay in Los Angeles?"
                [:p "If you haven’t already booked a room, we recommend
                finding a place in Northeast Los Angeles via AirBnB."])
             (q "What should I wear?!"
                [:p "Cocktail attire"])
             (q "Is there parking???"
                [:p "Yes. There will be valet parking at the Fig House, and a "
                 [:a {:href "https://www.parkme.com/lot/99490/611-south-carondolet-parking-los-angeles-ca"}
                  "parking garage"]
                 " next to the Carondelet House. That said, Uber
                 and/or Lyft are quickly becoming staples of Los Angeles
                 transportation, so if you’ll be imbibing, we’d recommend
                 one of those. We’ll also be running a shuttle from the
                 Langham Huntington Hotel in Pasadena, to both the rehearsal
                 dinner and to the wedding. (Details forthcoming.)"])
             (q "Should I rent a car?"
                [:p "It’s Los Angeles so... probably. Although a lot of
                people claim to get around solely with a car share service."])]]]}
   {:slug "registry"
    :title "Registry"
    :html [:div#text-inner
           [:h3 "Registry"]
           [:p
            "We know some of you will be traveling many miles
            to share our big day, and some will be sending warm wishes
            from afar. Your love and support are the best gifts we could
            ask for, as we begin a new chapter together!"]
           [:p
            "If you would like to give a gift "
            [:a {:href "https://www.zola.com/registry/dianaandrob"}
             "we are registered at Zola."]]]}])

(defn page
  ([html]
    (page nil html false))
  ([{:keys [slug title js css]} html wrap?]
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
        (for [[title slug width]
              [["Diana & Rob" "about" "30%"]
               ["Details" "details" "20%"]
               ["Registry" "registry" "20%"]
               ["Los Angeles" "what-to-do-in-la" "30%"]]]
          [:div.nav-item-container {:style (str "width:" width)}
           [:a.nav-item {:href (str "/" slug)}
            [:span title]]])]]
      (if html
        (if wrap?
          [:div#content-outer
           [:div#content
            html]]
          html))])))

(do
  (page
    [:h3.large "Diana & Rob are getting married!"])
  (doall
    (for [p pages]
      (page p (:html p) true))))