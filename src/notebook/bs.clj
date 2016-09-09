(ns notebook.bs
  (:require [notebook.html :as html]
            [notebook.gdoc :as gdoc]
            [net.cgrand.enlive-html :as h]
            [clojure.data.json :as json]))

(def site "britstensondesign.com")
(def img-options {:site site :image-dir "images"})
(def id "1L91imUDSrfm-HzJMTdX3czD55SzyTi2-Iue4Wf8nS7A")

(defn pull-doc [save-images?]
  (let [res (gdoc/fetch-html id (assoc img-options :save save-images?))
        [intro imgs] (:els res)]
    {:intro (gdoc/->html intro)
     :imgs (h/select imgs [:img])}))

(defn image
  ([src]
    (image src []))
  ([src colors]
   (let [->rgb #(apply format "rgb(%s,%s,%s)" %)]
     [:div.carousel-cell {:data-colors (json/write-str (map ->rgb colors))}
      [:div.img {:style (format "background-image:url(/images/%s.jpg)" src)}]])))

(defn print-site [#_{:keys [intro imgs]}]
  (html/refresh
    site
    "Brit Stenson Design"
    {:styles ["styles/flickity"
              "style"]
     :scripts ["https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min"
               "scripts/jquery.color.min"
               "scripts/flickity.pkgd.min"
               "scripts/script"]
     :typekit "fgc0zwz"
     :mobile-width "device-width"
     ;:jquery 3
     }
    [:div#outer-container
     [:div#container
      [:div#header-outer
       [:div#header-width
        [:div#header
         [:div.logo "Brit Stenson " [:em "Design"]]]]]
      [:div#content
       [:div.carousel {:data-flickity (json/write-str {:wrapAround true})}
        (image "hai-tang" [[74 125 182] [227 195 149]])
        (image "pine-rock" [[128 111 41] [251 216 214]])
        (image "dragon-lake" [[141 139 18] [0 0 0]])
        (image "grandview" [[103 124 67] [0 0 0]])
        (image "opening" [[155 163 0] [0 0 0]])
        (image "wadi" [[110 111 79] [0 0 0]])]
       [:div#bottom
        [:div.introduction]]]]]))

(print-site #_(pull-doc false))