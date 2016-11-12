(ns notebook.jia
  (:require [notebook.html :as html]
            [notebook.jia.recipes :as r]
            [clojure.string :as string]))

(defn recipe [slug]
  (let [recipe (r/decompose (:els (r/fetch slug false)))]
    [:div.post-preview.recipe
     [:div.post-preview-inner
      [:a {:href (format "/recipes/%s" (name slug))}
       [:h3 (:english (:titles recipe))]]
      [:div (:introduction (:text recipe))]]]))

(defn section [title href]
  [:a.section
   {:href href
    :style (format "background-image:url(/doodles/%s.png)" (string/lower-case title))}
   [:div.section-inner [:span.title title]]])

(defn layout [page content]
  (html/refresh
    (str "jiacookbook.com" (if page (str "/" page)))
    "Jia!"
    {:styles ["/style"]
     :scripts ["https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min"
               "jquery.color.min"
               ;"script"
               ]
     :typekit "ade3qww"
     :analytics "UA-83793620-1"}
    [:div#container
     [:div#top.clearfix
      [:div#header
       [:a#home {:href "/"} [:h1.jia "Jia!"]]
       [:h3.slogan "Teoswa food across the world"]]
      [:div#sections
       [:div#sections-inner
        (section "Shop" "/tag/shop")
        (section "Recipes" "/tag/recipes")
        (section "News" "/tag/news")
        (section "Kickstarter" "https://kickstarter.com/projects/1300940765/jia-eating-at-the-swatow-table")]]
      [:div#content
       [:div#content-inner
        content]]
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

(defn kickstarter-era []
  (let [recipes (list
                  (recipe :sa-de-bolognese)
                  (recipe :basil-cockles))]
    [(layout nil recipes)
     (layout "tag/recipes" recipes)
     (layout "tag/shop" [:h5.soon "Coming soon!"])
     (layout "tag/news" [:h5.soon "Coming soon!"])]))

