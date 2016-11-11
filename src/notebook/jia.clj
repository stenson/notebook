(ns notebook.jia
  (:require [notebook.html :as html]))

(defn kickstarter-era []
  (html/refresh
    "jiacookbook.com"
    "Jia!"
    {:styles [#_"//cloud.typenetwork.com/projects/373/fontface"
              "style"]
     :scripts ["https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min"
               "jquery.color.min"
               ;"script"
               ]
     :typekit "ade3qww"
     :analytics "UA-83793620-1"}
    (list
      [:div#map]
      [:div#color
       [:div
        [:h1 "Jia!"]
        [:h3 "Eating at the Swatow table"]
        [:h4 "by " "Diana " [:span.cn "丹霞"] " Zheng"
         " ( "
         #_[:a {:href "https://twitter.com/ddanxia"} "Twitter"]
         #_", "
         [:a {:href "https://www.instagram.com/ddanxia/"} "Instagram"]
         " )"]
        [:h4 "& " "Rob " [:span.cn "萝卜"] " Stenson"
         " ( " [:a {:href "https://twitter.com/robstenson"} "Twitter"]
         ", " [:a {:href "http://robstenson.com"} "Website"]
         " )"]
        [:a.kickstarter
         {:href "https://www.kickstarter.com/projects/1300940765/jia-eating-at-the-swatow-table"}
         "Check out our Kickstarter!"]
        [:a.sample
         {:href "/recipes/sa-de-bolognese"}
         "Check out our sample recipe for sa-de bolognese!"]]])))

