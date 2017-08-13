(ns notebook.gwanim
  (:require [notebook.html :as html]
            [me.raynes.fs :as fs]
            [clojure.java.shell :as shell]
            [clojure.string :as string]))

(defn dimensions [file]
  (->> (str file)
       (shell/sh "identify" "-format" "[%[fx:w],%[fx:h]]")
       (:out)
       (read-string)))

(defn images []
  (->> (fs/file "sites/gwan-im.com/assets")
       (file-seq)
       (remove fs/directory?)
       (map (fn [f]
              (let [[w h] (dimensions f)]
                [:img.asset
                 {:id (fs/name f)
                  :width (/ w 2)
                  :height (/ h 2)
                  :src (str "/assets/" (fs/base-name f))}])))
       (doall)))

(html/refresh
  (str "gwan-im.com")
  "Gwan-im"
  {:styles ["/style"]
   :scripts [#_"https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min"
             #_"jquery.color.min"
             #_"script"]
   :typekit "cfz1kjh"
   ;:analytics "UA-83793620-1"
   }
  [:body
   {:style "background:white"}
   #_[:div.images (images)]
   [:div#bg
    [:h3#hi
     {:class "tk-eloquent-jf-pro"}
     "hi " [:span "[at]"] " gwan-im " [:span "[dot]"] " com"]]])