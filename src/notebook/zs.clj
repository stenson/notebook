(ns notebook.zs
  (:require [clojure.string :as string]
            [puget.printer :refer [cprint]]
            [notebook.gdoc :as gdoc]
            [hiccup.page :refer [html5]]
            [notebook.html :as html]
            [notebook.hiero :refer [<-txt as-txt]]))

;(def map "http://www.bigmapblog.com/2013/birdseye-view-of-los-angeles/")

; Notable Events of June 25
; Recording of Sundays at the Village Vanguard
; Birthdays
; Deathdays
; International year of the pulse (http://www.fao.org/pulses-2016/en/)

(def deets
  ["___"
   "### Future"
   "So for now, Los Angeles is home (the background
    of this site is the upward view from our driveway),
    and we’ve decided to get married. Yes, we’ve
    been engaged for over a year now, and we’ve
    been cohabitating for over 5 years, and dating
    for almost 9. But there’s no time like the
    present!"
   "So, here are the deets:"
   "- _The day_ —  June 25, 2016"
   "- _The venue_ — The Carondelet House, Los\nAngeles, CA"
   "On the 24th, Brit & Kate will be hosting
    a Rehearsal Dinner at the Fig House in Los
    Angeles. Given that over 90% of all you wedding
    guests are non-Angelenos, you’re also all
    invited to that soiree — a more casual affair,
    complete with an accordion & clarinet duo,
    arranged in honor of the music once heard
    at Brit & Kate’s own wedding (not their idea;
    Rob’s idea)."])

(def site "zhengstenson.com")

(defn txt [f]
  (->> (format "sites/%s/txt/%s.txt" site (name f))
       (slurp)
       (<-txt)))

(html/refresh
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