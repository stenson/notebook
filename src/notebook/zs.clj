(ns notebook.zs
  (:require [clojure.string :as string]
            [puget.printer :refer [cprint]]
            [notebook.gdoc :as gdoc]
            [hiccup.page :refer [html5]]
            [notebook.css :refer [ß ß+]]
            [notebook.html :as html]
            [notebook.hiero :refer [<-txt as-txt]]
            [clojure.java.shell :refer [sh]]))

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
  [{:slug "recipes"
    :title "Recipes"
    :html
    [:div#text-inner
     [:div {:style "height:250px"}
      #_[:div.photo {:style "width:50%;float:left"} [:img {:src "/assembly.gif"}]]
      #_[:div.photo {:style "width:33%"} [:img {:src "/close.gif"}]]
      [:div.photo {:style "width:50%;margin:auto auto"} [:img {:src "/redbraise2.gif"}]]]
     [:div.text (txt :recipe)]]}
   {:slug "about"
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
                 [:li.small "All are invited!"]])
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
                 " next to the Carondelet House. Simply say you’re there for a wedding at the Carondelet House next door, we have 20 pre-paid spots reserved (though there will be more spots available). That said, Uber
                 and/or Lyft are quickly becoming staples of Los Angeles
                 transportation, so if you’ll be imbibing, we’d recommend
                 one of those. <strike>We’ll also be running a shuttle from the
                 Langham Huntington Hotel in Pasadena, to both the rehearsal
                 dinner and to the wedding. (Details forthcoming.)</strike> Ok, so it turns out the only fast way to drive between the venue and the hotel
                 is so old (the oldest highway in Los Angeles), that you can’t run shuttles on it. Very sorry for the late notice! But Ubers and Lyfts
                 are plentiful (and can be shared)."])
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
              [["Diana & Rob" "about" "24%"]
               ["Details" "details" "17%"]
               ["Registry" "registry" "17%"]
               ["Los Angeles" "what-to-do-in-la" "24%"]
               ["Program" "program" "18%"]]]
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
    (list
      [:h3.large
       "Diana & Rob are getting married!"]
      [:a.big-link
       {:href "/program"
        :style "margin-top:100px;"}
       "Click here to see the Wedding Program!"]
      [:a.big-link
       {:href "/recipes"
        :style "margin-bottom:200px;"}
       "Click here to see how to use your spices!"]))
  (doall
    (for [p pages]
      (page p (:html p) true))))

(defn person
  ([name]
    [:div.person [:strong name]])
  ([role name number]
   [:div.person
    [:em role]
    [:span (repeat number ".")]
    [:strong name]]))

(defn spacer []
  [:div.spacer])

(defn program [url]
  (html/refresh
    (str url)
    (str "Zheng & Stenson Wedding — Program")
    {:styles ["/klim" "/program"]
     :mobile-width "500"}
    [:div#container
     [:div#content-outer
      [:div#content
       [:a#home {:href "http://zhengstenson.com"}
        [:img#logo {:src "/sz-circle.png" :alt "Zheng & Stenson"}]
        [:h3 "Zheng & Stenson"]]
       [:h1 "Events"]
       [:div#events
        (person "5:00 - 5:30" "Guest Arrival" 37)
        (person "5:30 - 6:00" "Ceremony" 42)
        (person "6:00 - 7:00" "Cocktail Hour" 35)
        (person "6:10 - 6:30" "Tea Ceremony" 35)
        (person "7:00 - 8:30" "Dinner" 48)
        (person "8:30 - 11:00" "Dancing" 44)
        (person "9:00" "Cake Cutting" 47)]
       [:h1 "Cast of Characters"]
       [:div#people
        (person "Bride" "Diana Danxia Zheng" 32)
        (person "Groom" "Robert Rush Stenson" 28)
        (spacer)
        (person "Maid of Honor" "Lisa Zheng" 34)
        (person "Best Man" "Jack P. Stratton" 34)
        (spacer)
        (person "Bridesmaids" "Stefanie L. Koenig" 25)
        (person "Rebecca H. Yae")
        (person "Molly D. Stenson")
        (spacer)
        (person "Groomsmen" "William A. Stenson" 25)
        (person "Michael J. Molina")
        (person "Jeffrey R. Schwartz")
        (spacer)
        (person "Flower Girl" "Quincey McAvoy" 30)
        (person "Ring Bearer" "Baron McAvoy" 32)
        (spacer)
        (person "Father of the Bride" "Yuepeng Zheng" 20)
        (person "Mother of the Bride" "Kun Liu" 32)
        (spacer)
        (person "Father of the Groom" "Robert Stenson" 17)
        (person "Mother of the Groom" "Kathleen H. Stenson" 8)
        (spacer)
        (person "Officiant" "Theodore Katzman" 29)]
       [:h1 "Music"]
       [:div#music
        (person "Processional" "" 0)
        (spacer)
        (person "(Wedding Party)" "“Nocturne in E-Flat Major”" 4)
        (person "Ernest Ranglin")
        (spacer)
        (person "(Bride)" "“The Frim-Fram Sauce”" 25)
        (person "The Nat King Cole Trio")
        (spacer)
        (person "Recessional" "“My Daily Food”" 30)
        (person "The Maytals")
        (spacer)
        (person "Live Band" "The Vulfpeck" 38)
        (spacer)
        (person "DJs" "Andrew Schneiderman" 30)
        (person "Lloyd Cargo")]
       [:h1 "Wedding Favors"]
       [:div#favors
        (person "Tingly Spice Blend" [:a {:href "/recipes"} "How do I use this?"] 15)
        (person "Centerpieces" "Fruit & Flowers" 30)
        (person [:super [:sub "囍"] "Totebags"] "For your favors" 31)]
       [:h1 "Typefaces"]
       [:div#typefaces
        (person "Text" "Tiempos Text" 46)
        (person "Headers" "Founders Grotesk X-Condensed" 7)]]]]))

;(program "zhengstenson.com/program")
;(sh "ditto" "sites/zhengstenson.com/" "sites/dzrs.us")
;(program "dzrs.us")

(html/refresh
  "dzrs.us/photos"
  "Thank you!"
  {:styles ["style"]
   :device-width "device-width"
   :typekit "dnx4rph"}
  [:div#content
   [:div#text
    [:h1 "Thank you for celebrating with us!"]
    [:a.photos {:href "http://kassia.pixieset.com/dianaandrobrehearsaldinner/"}
     "Photos from the rehearsal dinner!"]
    [:a.photos {:href "http://kassia.pixieset.com/dianarobmarried/"}
     "Photos from the wedding!"]
    [:a.photos {:href "https://petitepix.com/gallery/diana-rob/"}
     "Animated gifs from the photo booth!"]
    [:a.photos {:href "http://grimacefilms.tv/diana-rob"}
     "A short film of the wedding!"]]])