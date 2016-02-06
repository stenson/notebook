(ns notebook.swatow.index
  (:require [notebook.html :as html]
            [garden.core :as garden]))

(html/refresh
  "swatow.co"
  "Swatow.co"
  {:styles (garden/css [:body {:background "white"}])}
  [:div#container
   [:div#logo
    [:img.logo {:src "/img/swatow-square.png" :alt "Swatow.co"}]]])