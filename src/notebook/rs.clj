(ns notebook.rs
  (:require [notebook.html :as html]))

(html/refresh
  "robstenson.com"
  "Rob Stenson"
  {:styles  ["styles"]
   :scripts []}
  [:div#container
   [:h1 "Hello World"]])