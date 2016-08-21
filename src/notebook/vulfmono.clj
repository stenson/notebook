(ns notebook.vulfmono
  (:require [notebook.html :as html]
            [notebook.hiero :as hiero]))

(def site "robstenson.com/articles/vulfmono")

; sounds of selectric https://www.youtube.com/watch?v=SF6Rj1FGCeo

(html/refresh
  site
  "mono.vulf.de"
  {:styles ["styles"]
   :scripts []}
  [:div#container
   [:div#content
    [:div#text
     [:h1 "vulf mono"]
     (hiero/slurp&parse site :essay)]]])