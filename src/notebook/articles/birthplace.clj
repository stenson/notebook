(ns notebook.articles.birthplace
  (:require [notebook.html :as html]))

(html/refresh
  "robstenson.com/articles/birthplaces"
  "Congressional Birthplaces"
  {:styles ["styles"
            "fonts"
            "https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,400italic"]
   :scripts ["//d3js.org/queue.v1.min"
             "//d3js.org/d3.v4.min"
             "//d3js.org/topojson.v1.min"
             "site"]}
  [:div#container])