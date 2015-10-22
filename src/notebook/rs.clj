(ns notebook.rs
  (:require [notebook.html :as html]))

(defn essay [title subtitle link where about date]
  [:li
   [:a {:href   link
        :target "_blank"}
    [:span.title title]
    (when subtitle [:span.subtitle subtitle])
    [:span.about about]
    [:span.date date]
    [:span.pipe "|"]
    [:span.where where]]])

(html/refresh
  "robstenson.com"
  "Rob Stenson"
  {:styles  ["styles" "fonts"]
   :scripts []}
  [:div#container
   [:div#outer-outer
    [:div#outer
     [:div#inner
      [:ul
       (essay "How to Get Hired at Twitter in 2011" nil
              "https://medium.com/@robstenson/how-to-get-hired-at-twitter-in-2011-22f03e8082ab#.a1hli53ft"
              "medium.com"
              "Ruminations and useless advice from my stint in Silicon Valley"
              "Aug 21, 2015")
       (essay "Lossy’s Uncharted Waters" nil
              "http://tonal.goodhertz.co/uncharted-waters/"
              "tonal.goodhertz.co"
              "Unnecessarily poetic blog post about a piece of audio software"
              "Apr 7, 2015")
       (essay "Why I Play the Banjo" nil
              "https://medium.com/the-banjo/why-i-play-the-banjo-3e312da0eab7"
              "medium.com"
              "A brief essay, mostly about my dad"
              "Jan 30, 2015")
       (essay "Churches Hidden in Plain Text"
              "Mining and Mapping the Historiography of Gothic Architecture"
              "/pdfs/thesis.pdf"
              "academic"
              "My senior thesis from college"
              "Apr 5, 2010")
       (essay "R(o)ygb(i)v"
              "Horizontal Color in the New York Subway"
              "/pdfs/roygbiv.pdf"
              "academic"
              "A paper I once presented at the TAG conference in 2008"
              "Oct 25, 2007")]]]
    [:div#cameo
     [:div#cameo-inner]
     [:img#rob {:src "me.png"}]]]])