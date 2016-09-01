(ns notebook.jia.recipes
  (:require [notebook.gdoc :as gdoc]
            [net.cgrand.enlive-html :as h]
            [clojure.string :as string]
            [notebook.html :as html]))

(def test-id "1fh-YHwH3GI6TDyK61KFrhaeKc9IpuMLJxWRtvlTtZ6g")

#_(def x (gdoc/read-html (gdoc/g-dld test-id :html)))

(defn process-grafs [[bold italic] gs]
  (->> gs
       (map (fn [{:keys [content] :as p}]
              (let [txt (h/text p)]
                (if (empty? txt)
                  {:tag :div :attrs {:class "spacer"} :content ""}
                  (assoc
                    p :content
                      (map (fn [{:keys [attrs content] :as el}]
                             (if (= (:class attrs) bold)
                               {:tag :strong :attrs nil :content content}
                               (if (= (:class attrs) italic)
                                 {:tag :em :attrs nil :content content}
                                 el)))
                           content))))))
       (h/emit*)
       (string/join)
       #_(map h/text)
       #_(string/join "\n")
       #_(string/trim-newline)
       ))

(defn parse-css [css]
  (let [unwrap (fn [xs] (string/replace (first (first xs)) #"^\." ""))
        rules (->> (string/split (first (:content css)) #"\}")
                   (map #(string/split % #"\{"))
                   (filter (fn [[sel]] (re-matches #"\.c.*" sel))))
        bold (filter (fn [[_ rules]] (= rules "font-weight:700")) rules)
        italic (filter (fn [[_ rules]] (= rules "font-style:italic")) rules)]
    [(unwrap bold)
     (unwrap italic)]))

(defn breakdown-html [x]
  (let [style (parse-css (first (h/select x [:style])))
        ps (h/select x [:p])
        [characters pinyin english img & text] ps
        [ingredients
         _ instructions] (->> (drop 1 text)
                              (partition-by #(= (h/text %) "Instructions")))]
    {:title {:characters (h/text characters)
             :pinyin (h/text pinyin)
             :english (h/text english)}
     :img (-> (h/select img [:img])
              (first)
              (get-in [:attrs :src]))
     :ingredients (process-grafs style ingredients)
     :instructions (process-grafs style instructions)}))

(defn set-html [x]
  (let [{:keys [title img ingredients instructions]} (breakdown-html x)
        slug (string/lower-case (string/replace (:english title) #"\s" "-"))]
    (html/refresh
      (str "jiacookbook.com/recipes/" slug)
      (str "Jia! — " (:english title))
      {:styles ["/recipe"]
       :scripts []
       :typekit "ade3qww"}
      [:div#container
       [:div#recipe-container
        [:div#topbar
         [:a#logo {:href "/"} "Jia!"]]
        [:div#recipe
         [:div#title
          [:h1.cn (:characters title)]
          [:h1.en (:english title)]
          [:h4.pinyin (string/lower-case (:pinyin title))]]
         [:div#photo {:style (format "background-image:url(%s)" img)}]
         [:div.sep]
         [:div#text
          [:div#ingredients
           ingredients]
          [:div#instructions
           instructions]]]]
       [:div#footer-container
        [:div#footer
         [:p "Copyright © 2016 by Diana Zheng & Rob Stenson"]
         [:p "All rights reserved"]]]])))