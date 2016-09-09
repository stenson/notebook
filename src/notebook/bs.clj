(ns notebook.bs
  (:require [notebook.html :as html]
            [notebook.gdoc :as gdoc]
            [net.cgrand.enlive-html :as h]))

(def site "britstensondesign.com")
(def img-options {:site site :image-dir "images"})
(def id "1L91imUDSrfm-HzJMTdX3czD55SzyTi2-Iue4Wf8nS7A")

(defn pull-doc [save-images?]
  (let [res (gdoc/fetch-html id (assoc img-options :save save-images?))
        [intro imgs] (:els res)]
    {:intro (gdoc/->html intro)
     :imgs (h/select imgs [:img])}))

(defn print-site [{:keys [intro imgs]}]
  (html/refresh
    "britstensondesign.com"
    "Brit Stenson Design"
    {:styles ["style"]
     :scripts []
     :typekit "fgc0zwz"}
    [:div#outer-container
     [:div#container
      [:div#header-outer
       [:div#header-width
        [:div#header
         [:div.logo "Brit Stenson Design"]]]]
      [:div#content
       [:div.slideshow.clearfix
        (for [i imgs]
          [:div.image {:style (format "background-image:url(%s)" (:src (:attrs i)))}])]
       #_[:div.introduction intro]]]]))

(print-site (pull-doc false))