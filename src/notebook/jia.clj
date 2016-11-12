(ns notebook.jia
  (:require [notebook.html :as html]
            [notebook.jia.recipes :as r]))

(defn recipe [slug]
  (let [recipe (r/decompose (:els (r/fetch slug false)))]
    [:div.post-preview.recipe
     [:div.post-preview-inner
      [:a {:href (format "/recipes/%s" (name slug))}
       [:h3 (:english (:titles recipe))]]
      [:div (:introduction (:text recipe))]]]))

(defn kickstarter-era []
  (html/refresh
    "jiacookbook.com"
    "Jia!"
    {:styles ["style"]
     :scripts ["https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min"
               "jquery.color.min"
               ;"script"
               ]
     :typekit "ade3qww"
     :analytics "UA-83793620-1"}
    [:div#container
     [:div#top.clearfix
      [:div#header
       [:h1.jia "Jia!"]
       [:h3.slogan "Teoswa food across the world"]]
      [:div#sections
       [:div#sections-inner
        [:a.section {:href ""} [:div.section-inner "Shop"]]
        [:a.section {:href "/recipes/sa-de-bolognese/"} [:div.section-inner "Recipes"]]
        [:a.section {:href ""} [:div.section-inner "News"]]
        [:a.section {:href "https://kickstarter.com/projects/1300940765/jia-eating-at-the-swatow-table"} [:div.section-inner "Kickstarter"]]]]
      [:div#content
       [:div#content-inner
        (recipe :sa-de-bolognese)
        (recipe :basil-cockles)]]
      [:div#drawings
       [:div#drawings-inner]]]
     [:div#footer-container
      [:div#footer
       [:p "Copyright © 2016 by "]
       [:p {:style "margin-top:6px"} "Diana " [:span.cn "丹霞"] " Zheng"
        " (" [:a {:href "https://www.instagram.com/ddanxia/"} "Instagram"] ")"]
       [:p {:style "margin-bottom:6px"}
        "& " "Rob " [:span.cn "萝卜"] " Stenson"
        " (" [:a {:href "https://twitter.com/robstenson"} "Twitter"]
        ", " [:a {:href "http://robstenson.com"} "Website"] ")"]
       [:p.rights
        "All rights reserved"]]]]))

