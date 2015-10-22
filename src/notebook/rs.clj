(ns notebook.rs
  (:require [notebook.html :as html]))

(defn essay [title link about date]
  [:li
   [:a {:href   link
        :target "_blank"}
    [:span.title title]
    [:span.about about]
    [:span.date date]]])

(html/refresh
  "robstenson.com"
  "Rob Stenson"
  {:styles  ["styles" "fonts"]
   :scripts []}
  [:div#container
   [:div#outer
    [:div#inner
     [:ul
      (essay "How to Get Hired at Twitter in 2011"
             "https://medium.com/@robstenson/how-to-get-hired-at-twitter-in-2011-22f03e8082ab#.a1hli53ft"
             "Ruminations and useless advice from my stint in Silicon Valley"
             "Aug 21, 2015")
      (essay "Why I Play the Banjo"
             "https://medium.com/the-banjo/why-i-play-the-banjo-3e312da0eab7"
             "A brief essay, mostly about my dad"
             "Jan 30, 2015")
      (essay "Churches Hidden in Plain Text — Mining and Mapping the Historiography of Gothic Architecture"
             "/pdfs/thesis.pdf"
             "My senior thesis from college"
             "Apr 5, 2010")
      (essay "R(o)ygb(i)v — Horizontal Color in the New York Subway"
             "/pdfs/roygbiv.pdf"
             "A paper I once presented at the TAG conference in 2008"
             "Oct 25, 2007")]]]])