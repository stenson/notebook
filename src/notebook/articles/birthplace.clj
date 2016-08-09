(ns notebook.articles.birthplace
  (:require [notebook.html :as html]))

(html/refresh
  "robstenson.com/articles/birthplaces"
  "Congressional Birthplaces"
  {:styles ["https://api.tiles.mapbox.com/mapbox-gl-js/v0.21.0/mapbox-gl"
            "https://fonts.googleapis.com/css?family=Source+Sans+Pro:400,900,400italic"
            "styles"
            "fonts"]
   :scripts [#_"//d3js.org/queue.v1.min"
             #_"//d3js.org/d3.v4.min"
             #_"//d3js.org/topojson.v1.min"
             #_"site"
             "https://api.tiles.mapbox.com/mapbox-gl-js/v0.21.0/mapbox-gl"
             "map"]}
  [:div#container
   [:div#map]])