(ns notebook.jia.recipes
  (:require [notebook.gdoc :as gdoc]
            [net.cgrand.enlive-html :as h]
            [clojure.string :as string]
            [notebook.html :as html]))

(def ids
  (->> [[:basil-cockles "1fh-YHwH3GI6TDyK61KFrhaeKc9IpuMLJxWRtvlTtZ6g"]]
       (into {})))

; markdown
; pull images from remote
; trim empty paragraphs at breakpoints

(defn split-els [els]
  (partition-by #(= :hr (:tag %)) els))

(defn decompose [ps]
  (let [[header _ introduction _ tips _ ingredients _ instructions] (split-els ps)
        [characters pinyin english img] header]
    {:titles {:characters (h/text characters)
              :pinyin (string/lower-case (h/text pinyin))
              :english (h/text english)
              :img (gdoc/pluck-src img)}
     :text {:introduction (gdoc/->html introduction)
            :tips (gdoc/->html tips)
            :ingredients (gdoc/->html ingredients)
            :instructions (gdoc/->html instructions)
            ;:etymology (gdoc/->html etymology)
            }}))

(defn set-html [slug ps]
  (let [{:keys [titles text]} (decompose ps)]
    (html/refresh
      (str "jiacookbook.com/recipes/" (name slug))
      (str "Jia! — " (:english titles))
      {:styles ["/recipe"]
       :scripts []
       :typekit "ade3qww"
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
           {:style (format "background-image:url(%s)" (:img titles))}]
          [:div.sep]
          [:div#text
           [:div.top.clearfix
            [:div#introduction (:introduction text)]
            [:div#tips (:tips text)]]
           [:div.split]
           [:div.bottom.clearfix
            [:div#ingredients (:ingredients text)]
            [:div#instructions (:instructions text)]]
           (if (not (empty? (:etymology text)))
             [:div#etymology (:etymology text)])]]]
        [:div#footer-container
         [:div#footer
          [:p "Copyright © 2016 by Diana Zheng & Rob Stenson"]
          [:p.rights
           "All rights reserved"]]]]])))

(defn pull [slug]
  (let [remote (gdoc/fetch-html (get ids slug))]
    (set-html slug (:ps remote))))