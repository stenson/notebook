(ns notebook.rs
  (:require [notebook.html :as html]
            [notebook.gdoc :as gdoc]
            [notebook.hiero :as hiero]))

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
   :scripts ["hyphenator"]}
  [:div#container
   [:div#outer-outer
    [:div#intro "Recent Writings"]
    [:div#outer
     [:div#inner
      [:ul
       (essay "Through the Sea, 1945" nil
              "https://medium.com/@robstenson/through-the-sea-1945-235acff7d406"
              "medium.com"
              "Two ensigns consider their own deaths during the Battle of Okinawa"
              "Feb 9, 2016")
       (essay "Searching on Interstate 10" nil
              "http://lit.vulf.de/interstate-10/"
              "lit.vulf"
              "“We moved westward, downloading obscure Wikipedia articles...”"
              "Oct 27, 2015")
       (essay "How to Get Hired at Twitter in 2011" nil
              "https://medium.com/@robstenson/how-to-get-hired-at-twitter-in-2011-22f03e8082ab#.a1hli53ft"
              "medium.com"
              "Ruminations and useless advice from a stint in Silicon Valley"
              "Aug 21, 2015")
       (essay "Lossy’s Uncharted Waters" nil
              "http://tonal.goodhertz.co/uncharted-waters/"
              "tonal.goodhertz.co"
              "Unnecessarily poetic blog post about a piece of audio software"
              "Apr 7, 2015")
       (essay "Why I Play the Banjo" nil
              "https://medium.com/the-banjo/why-i-play-the-banjo-3e312da0eab7"
              "medium.com"
              "(TL;DR my dad plays the banjo)"
              "Jan 30, 2015")
       (essay "VULF0004 (Beastly 45\") Liner Notes" nil
              "/vulf0004.jpg"
              "vinyl record"
              "Semi-fictional liner notes written in 45 minutes"
              "Apr 4, 2013")
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
     [:img#rob {:src "me.png"}]]
    [:div#about
     (:html
       (hiero/parse-p
         "Rob Stenson writes words, code, and clawhammer banjo tunes.
         He is a partner at [Goodhertz, Inc.](https://goodhertz.co)
         and a recording artist on the [Vulf Records](https://vulf.bandcamp.com/)
         label. Though previously a resident of (in chronological order)
         Jacksonville, Cleveland, New York, San Francisco, and Boston,
         Rob currently lives among palm tree emoji in Los Angeles, CA
         with his fiancée and a dog named Alphonso. He also has a
         [Twitter account](https://twitter.com/robstenson)."))]]])