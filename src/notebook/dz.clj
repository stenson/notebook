(ns notebook.dz
  (:require [notebook.html :as html]))

(html/refresh
  "dianazheng.com"
  "Diana Zheng"
  {:styles ["styles"]}
  [:div#container
   [:h1 "Jia!"]])