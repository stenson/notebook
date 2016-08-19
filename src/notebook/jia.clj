(ns notebook.jia
  (:require [notebook.html :as html]))

(html/refresh
  "jiacookbook.com"
  "Jia!"
  {:styles ["style"]
   :scripts ["https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min"
             "jquery.color.min"
             "script"]}
  [:div
   [:h1 "Jia!"]
   [:h3 "a cookbook about the food of Swatow"]])