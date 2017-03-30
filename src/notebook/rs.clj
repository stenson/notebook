(ns notebook.rs
  (:require [notebook.html :as html]
            [notebook.gdoc :as gdoc]
            [notebook.hiero :as hiero]))

(defn video [slug title about date]
  [:li
   [:iframe.embed.youtube
    {:src (str "//www.youtube.com/embed/"
               slug
               "?modestbranding=1&autohide=1&showinfo=0&controls=0")}]
   [:div.desc
    [:span.title title]
    [:span.about about]
    [:span.date date]]])

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
  {:styles ["styles"
            #_"Hobeaux_web/font"]
   :scripts ["hyphenator"]
   ;:typekit "cmm5ckr"
   }
  [:div#container
   [:div#outer-outer
    [:div#intro "Unordered List of Recent Videos"]
    [:div#outer
     [:div#inner
      [:ul
       (video
         "kruASSL78m8"
         "Holy Trinities — Tambourine"
         "Jack Stratton’s three favorite tambourinists"
         "March 29, 2017")
       (video
         "JsA6WWO5D4c"
         "Holy Trinities — Guitar"
         "Jack Stratton’s three favorite guitarists"
         "January 17, 2017")
       (video
         "Xgv8EKwXvRA"
         "Goodhertz Visits Matthewdavid"
         "A visit with the founder of Leaving Records"
         "December 14, 2016")
       (video
         "jjx6Xzf2m2A"
         "Talking on Tape, with Devin Kerr"
         "Goodhertz co-founder Devin Kerr talks about tape"
         "November 22, 2016")
       (video
         "BJpF18Bsk5k"
         "What is a Plugin? Ep. 1"
         "An interview with James Edmondson of OHno Type Co."
         "November 15, 2016")
       #_(video
         "1O2WyuHj10M"
         "Wow Control — a Questionnaire"
         "A short in-class activity to try with your students"
         "September 30, 2016")
       (video
         "zgSGuJB5gKk"
         "Wow Control — a Poem of Presets"
         "A video-poem about some presets on an audio plugin"
         "September 28, 2016")
       #_(video
         "x3bAZiC37kc"
         "All the Bob Marley Drum Intros in Chronological Order"
         "A short video of reggae drum introductions"
         "April 26, 2016")
       #_(essay "Holy Trinities, Ep. 1 — Snare" nil
              "http://tonal.goodhertz.co/holy-trinities-snare/"
              "tonal.goodhertz.co"
              "A podcast I produced and edited about Jack Stratton’s favorite snares from snare history"
              "May 26, 2106")
       #_(essay "My First Instrument" nil
              "http://my1stinstrument.com"
              "my1stinstrument.com"
              "An ongoing series of interviews with musicians (+ gif portraits)"
              "April 5, 2016")
       #_(essay "Garbage in, Garbage out" nil
              "http://www.atlasobscura.com/articles/is-this-the-first-time-anyone-printed-garbage-in-garbage-out"
              "Atlas Obscura"
              "The early computing phrase’s history is rife with bad information"
              "March 14, 2016")
       #_(essay "Thrill of the Arts" nil
              "http://lit.vulf.de/thrill-of-the-arts/"
              "lit.vulf"
              "Liner notes written for Vulfpeck’s 2015 album"
              "March 3, 2016")
       #_(essay "League of Lagers"
              "An ongoing series"
              "http://lager.robstenson.com"
              "League of Lagers"
              "From the Emperor of Mexico and his personal brewers, to the Lt. General of Okinawa and his call for more concrete, a history of 5
              international lagers that all kind of taste the same"
              "March 24, 2016")
       #_(essay "Through the Sea, 1945" nil
              "https://medium.com/@robstenson/through-the-sea-1945-235acff7d406"
              "medium.com"
              "Two ensigns consider their own deaths during the Battle of Okinawa"
              "Feb 9, 2016")
       #_(essay "Searching on Interstate 10" nil
              "http://lit.vulf.de/interstate-10/"
              "lit.vulf"
              "“We moved westward, downloading obscure Wikipedia articles...”"
              "Oct 27, 2015")
       #_(essay "How to Get Hired at Twitter in 2011" nil
              "https://medium.com/@robstenson/how-to-get-hired-at-twitter-in-2011-22f03e8082ab#.a1hli53ft"
              "medium.com"
              "Ruminations and useless advice from a stint in Silicon Valley"
              "Aug 21, 2015")
       #_(essay "Lossy’s Uncharted Waters" nil
              "http://tonal.goodhertz.co/uncharted-waters/"
              "tonal.goodhertz.co"
              "Unnecessarily poetic blog post about a piece of audio software"
              "Apr 7, 2015")
       #_(essay "Why I Play the Banjo" nil
              "https://medium.com/the-banjo/why-i-play-the-banjo-3e312da0eab7"
              "medium.com"
              "(TL;DR my dad plays the banjo)"
              "Jan 30, 2015")
       #_(essay "VULF0004 (Beastly 45\") Liner Notes" nil
              "/vulf0004.jpg"
              "vinyl record"
              "Semi-fictional liner notes written in 45 minutes"
              "Apr 4, 2013")
       #_(essay "Churches Hidden in Plain Text"
              "Mining and Mapping the Historiography of Gothic Architecture"
              "/pdfs/thesis.pdf"
              "academic"
              "My senior thesis from college"
              "Apr 5, 2010")
       #_(essay "R(o)ygb(i)v"
              "Horizontal Color in the New York Subway"
              "/pdfs/roygbiv.pdf"
              "academic"
              "A paper I once presented at the TAG conference in 2008"
              "Oct 25, 2007")
       #_(essay "The Met Goes Greek... and Roman" nil
              "http://columbiaspectator.com/2007/04/25/met-goes-greekand-roman"
              "Columbia Spectator"
              "A review of the newly-renovated Greek and Roman galleries at the Met"
              "Apr 25, 2007")]]]
    [:div#cameo
     [:div#cameo-inner]
     [:img#rob {:src "me.png"}]]
    [:div#about
     (:html
       (hiero/parse-p
         "Rob Stenson makes short films and plays clawhammer banjo.
         He is a partner at [Goodhertz, Inc.](https://goodhertz.co)
         and a recording artist on the [Vulf Records](https://vulf.bandcamp.com/)
         label. Though previously a resident of (in chronological order)
         Jacksonville, Cleveland, New York, San Francisco, and Boston,
         Rob currently lives in Los Angeles, CA
         with his wife and a dog named Alfie. Rob also has a
         [Twitter account](https://twitter.com/robstenson)."))]
    [:div#colophon
     (list
       [:h3 "Colophon"]
       (:html
         (hiero/parse-p
           "This text of this website is set in [Hobeaux](http://www.ohnotype.co/product/hobeaux) by OHno Type Co.")))]]])