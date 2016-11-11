(ns notebook.jia.recipes
  (:require [notebook.gdoc :as gdoc]
            [net.cgrand.enlive-html :as h]
            [clojure.string :as string]
            [notebook.html :as html]))

(def ids
  (->> [[:basil-cockles "1fh-YHwH3GI6TDyK61KFrhaeKc9IpuMLJxWRtvlTtZ6g"]
        [:sa-de-bolognese "1JkaPO1vNeomSXvxb009mxGiToZ7K6yAE52CZkkvud90"]
        [:toc "18PRsKcQ4G6cWlcOxjU73P-Db1Pr8UyV3AgoPsFVnS1U"]]
       (into {})))

; markdown

(defn decompose [els]
  (let [[header introduction tips ingredients instructions extras] els
        [characters pinyin english img] header]
    {:titles {:characters (h/text characters)
              :pinyin (string/lower-case (h/text pinyin))
              :english (h/text english)
              :img (:attrs (first (h/select img [:img])))}
     :text {:introduction (gdoc/->html introduction)
            :tips (gdoc/->html tips)
            :ingredients (gdoc/->html ingredients)
            :instructions (gdoc/->html instructions)
            :extras (->> extras
                         (map (fn [n]
                                {:caption (h/text n)
                                 :image (:attrs (first (h/select n [:img])))})))}}))

(def site "jiacookbook.com/recipes")

(defn page [path title html-content]
  (html/refresh
    path
    (if title (str "Jia! — " title) "Jia!")
    {:styles ["/recipe"]
     :scripts []
     :typekit "ade3qww"
     :analytics "UA-83793620-1"
     :mobile-width "device-width"}
    [:div#outer-container
     [:div#container
      [:div#recipe-container
       [:div#topbar
        [:a#logo {:href "/"} "Jia!"]]
       html-content]
      [:div#footer-container
       [:div#footer
        [:p "Copyright © 2016 by Diana Zheng & Rob Stenson"]
        [:p.rights
         "All rights reserved"]]]]]))

(defn index []
  (page
    "jiacookbook.com"
    nil
    [:div#toc
     [:h1.cn "食!"]
     [:h1.en "Jia!"]
     [:h4.pinyin "Eating at the Swatow table"]
     [:h6 "A cookbook about the food of Swatow and its Diaspora"]]))

(defn set-html [path ps]
  (let [{:keys [titles text]} (decompose ps)]
    (page
      path (:english titles)
      [:div#recipe
       [:div#title
        [:h1.cn (:characters titles)]
        [:h1.en (:english titles)]
        [:h4.pinyin (:pinyin titles)]]
       [:div#photo
        {:style (html/bg-img (:src (:img titles)))}]
       [:div.sep]
       [:div#text
        [:div.top.clearfix
         [:div#introduction (:introduction text)]
         [:div.tilde "～"]
         [:div#tips-container
          (when-let [es (:extras text)]
            [:div#sidebar
             (for [e es]
               [:div.sidebar-image
                [:div.sidebar-image-img {:style (html/bg-img (:src (:image e)))}]
                [:div.sidebar-image-caption (:caption e)]])])
          [:div#tips (:tips text)]]]
        [:div.split]
        [:div.bottom.clearfix
         [:div#ingredients (:ingredients text)]
         [:div#instructions (:instructions text)]]]])))

(defn fetch [slug save?]
  (let [path (str site "/" (name slug))]
    (gdoc/fetch-html (get ids slug) {:site path :save save?})))

(defn pull [slug save?]
  (let [remote (fetch slug save?)]
    (set-html (:site (:options remote)) (:els remote))))

#_(pull :basil-cockles false)