(ns notebook.jia
  (:require [notebook.html :as html]
            [notebook.jia.recipes :as r]
            [clojure.string :as string]
            [notebook.gdoc :as gdoc]))

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
    :style (format "background-image:url(/images/%s.jpg)" (string/lower-case title))}
   [:div.section-inner [:span.title title]]])

(defn layout [page slogan content]
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
      [:div#sections
       [:div#sections-inner
        (section "Shop" "/tag/shop")
        (section "Recipes" "/tag/recipes")
        (section "News" "/tag/news")
        (section "Kickstarter" "https://kickstarter.com/projects/1300940765/jia-eating-at-the-swatow-table")
        (section "Instagram" "https://www.instagram.com/ddanxia/")]]
      [:div#content
       [:div#header
        [:a#home {:href "/"} [:h1.jia "Jia!"]]
        [:h3.slogan slogan]]
       [:div#content-inner
        content]]
      [:div#drawings
       [:div#drawings-inner]]]
     [:div#footer-container
      [:div#footer
       [:p "Copyright © 2016 by "]
       [:p {:style "margin-top:6px"} "Diana " [:span.cn "丹霞"] " Zheng"
        #_" (" #_[:a {:href "https://www.instagram.com/ddanxia/"} "Instagram"] #_")"
        "& " "Rob " [:span.cn "萝卜"] " Stenson"
        #_" (" #_[:a {:href "https://twitter.com/robstenson"} "Twitter"]
        #_", " #_[:a {:href "http://robstenson.com"} "Website"] #_")"]
       [:p.rights
        "All rights reserved"]]]]))

(defn frontmatter-text [els]
  [:div.post-preview.welcome
   [:div.post-preview-inner
    [:div (gdoc/->html els)]]])

(defn kickstarter-era []
  (let [matter (:els (r/fetch :home false))
        recipes (list
                  (recipe :sa-de-bolognese)
                  (recipe :basil-cockles))]
    [(layout nil "Teoswa food across the world" (frontmatter-text (nth matter 0)))
     (layout "tag/recipes" "Recipes" recipes)
     (layout "tag/shop" "Shop" (frontmatter-text (nth matter 1)))
     (layout "tag/news" "News" (frontmatter-text (nth matter 2)))]))

