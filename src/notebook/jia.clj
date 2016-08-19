(ns notebook.jia
  (:require [notebook.html :as html]))

(html/refresh
  "jiacookbook.com"
  "Jia!"
  {:styles ["style"]
   :scripts []}
  [:div
   [:h1 "Jia!"]
   [:h3 "A cookbook about the food of Swatow"]])