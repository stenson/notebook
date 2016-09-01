(ns notebook.jia
  (:require [notebook.html :as html]))

(html/refresh
  "jiacookbook.com"
  "Jia!"
  {:styles [#_"//cloud.typenetwork.com/projects/373/fontface"
            "style"]
   :scripts ["https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min"
             "jquery.color.min"
             "script"]
   :typekit "ade3qww"}
  (list
    [:div#map]
    [:div#color
     [:div
      [:h1 "Jia!"]
      [:h3 "Eating at the Swatow table"]
      [:h4 "by " "Diana " [:span.cn "丹霞"] " Zheng"
       " ( " [:a {:href "https://twitter.com/ddanxia"} "Twitter"]
       ", " [:a {:href "https://www.instagram.com/ddanxia/"} "Instagram"]
       " )"]
      [:h4 "& " "Rob " [:span.cn "萝卜"] " Stenson"
       " ( " [:a {:href "https://twitter.com/robstenson"} "Twitter"]
       ", " [:a {:href "http://robstenson.com"} "Website"]
       " )"]
      [:h2 "Kickstarter coming soon!"]]]))