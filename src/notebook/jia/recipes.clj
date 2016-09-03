(ns notebook.jia.recipes
  (:require [notebook.gdoc :as gdoc]
            [net.cgrand.enlive-html :as h]
            [clojure.string :as string]
            [notebook.html :as html]))

(def ids
  (->> [[:basil-clams "1fh-YHwH3GI6TDyK61KFrhaeKc9IpuMLJxWRtvlTtZ6g"]]
       (into {})))

#_(defn breakdown-html [{:keys [style ps] :as x}]
  (let [[characters pinyin english img & text] ps
        [ingredients _ plaintext] (partition-by #(= (h/text %) "Instructions") (drop 1 text))
        [instructions _ etymology] (partition-by #(= (h/text %) "Rob") plaintext)]
    {:title {:characters (h/text characters)
             :pinyin (h/text pinyin)
             :english (h/text english)}
     :img (-> (h/select img [:img])
              (first)
              (get-in [:attrs :src]))
     :ingredients (gdoc/process-grafs style ingredients)
     :instructions (gdoc/process-grafs style instructions)
     :etymology (gdoc/process-grafs style etymology)}))

(defn split-at-text [content ps]
  (partition-by #(= (h/text %) content) (drop 1 ps)))

(defn set-html [slug {:keys [style ps] :as x}]
  (let [[characters pinyin english img & text] ps
        [ingredients _ plaintext] (split-at-text "Instructions" (drop 1 text))
        [instructions _ etymology] (split-at-text "Rob" plaintext)
        english-title (h/text english)]
    (html/refresh
      (str "jiacookbook.com/recipes/" (name slug))
      (str "Jia! — " english-title)
      {:styles ["/recipe"]
       :scripts []
       :typekit "ade3qww"
       :mobile-width "device-width"}
      [:div#container
       [:div#recipe-container
        [:div#topbar
         [:a#logo {:href "/"} "Jia!"]]
        [:div#recipe
         [:div#title
          [:h1.cn (h/text characters)]
          [:h1.en english-title]
          [:h4.pinyin (string/lower-case (h/text pinyin))]]
         [:div#photo
          {:style (format "background-image:url(%s)" (gdoc/pluck-src img))}]
         [:div.sep]
         [:div#text
          [:div#ingredients
           (gdoc/process-grafs style ingredients)]
          [:div#instructions
           (gdoc/process-grafs style instructions)]
          (if (not (empty? etymology))
            [:div#etymology
             (gdoc/process-grafs style etymology)])]]]
       [:div#footer-container
        [:div#footer
         [:p "Copyright © 2016 by Diana Zheng & Rob Stenson"]
         [:p.rights
          "All rights reserved"]]]])))

(defn pull [slug]
  (let [remote (gdoc/fetch-html (get ids slug))]
    (set-html slug remote)))