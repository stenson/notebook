(ns notebook.jia.recipes
  (:require [notebook.gdoc :as gdoc]
            [net.cgrand.enlive-html :as h]
            [clojure.string :as string]
            [notebook.html :as html]))

(def ids
  (->> [[:basil-cockles "1fh-YHwH3GI6TDyK61KFrhaeKc9IpuMLJxWRtvlTtZ6g"]]
       (into {})))

; markdown

(defn decompose [ps]
  (let [[header _ introduction _ tips _ ingredients _ instructions] (gdoc/split-els ps)
        [characters pinyin english img] header]
    {:titles {:characters (h/text characters)
              :pinyin (string/lower-case (h/text pinyin))
              :english (h/text english)
              :img img}
     :text {:introduction (gdoc/->html introduction)
            :tips (gdoc/->html tips)
            :ingredients (gdoc/->html ingredients)
            :instructions (gdoc/->html instructions)}}))

(defn set-html [slug ps]
  (let [{:keys [titles text]} (decompose ps)
        slug-str (name slug)
        path (str "jiacookbook.com/recipes/" slug-str)]
    (gdoc/pluck&save-src (:img titles) (str "sites/" path "/" slug-str ".jpg"))
    (html/refresh
      path
      (str "Jia! — " (:english titles))
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
         [:div#recipe
          [:div#title
           [:h1.cn (:characters titles)]
           [:h1.en (:english titles)]
           [:h4.pinyin (:pinyin titles)]]
          [:div#photo
           {:style (format "background-image:url(%s.jpg)" slug-str)}]
          [:div.sep]
          [:div#text
           [:div.top.clearfix
            [:div#introduction (:introduction text)]
            [:div.tilde "～"]
            [:div#tips (:tips text)]]
           [:div.split]
           [:div.bottom.clearfix
            [:div#ingredients (:ingredients text)]
            [:div#instructions (:instructions text)]]]]]
        [:div#footer-container
         [:div#footer
          [:p "Copyright © 2016 by Diana Zheng & Rob Stenson"]
          [:p.rights
           "All rights reserved"]]]]])))

(defn pull [slug]
  (let [remote (gdoc/fetch-html (get ids slug))]
    (set-html slug (:ps remote))))

#_(pull :basil-cockles)