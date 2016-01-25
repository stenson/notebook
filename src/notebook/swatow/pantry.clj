(ns notebook.swatow.pantry
  (:require [notebook.html :as html]
            [notebook.gdoc :as gdoc]
            [puget.printer :as puget]))

#_(def book-links
  [["Little Guide Book" "http://www.amazon.com/gp/product/9814561290/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=9814561290&linkCode=as2&tag=swatpant-20&linkId=544XZ5IOV7ZQQF4U"]
   ["Little Guide Book" "http://www.amazon.com/gp/product/0684847396/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0684847396&linkCode=as2&tag=swatpant-20&linkId=TLRCL5UCZQORBAT2"]])

(def all-posts (atom []))

(defn get-all-posts []
  (reset! all-posts
          (->> ["1uXDM4acSAxXTcYtwqoSmZK7XDxIWP7l40Ne6t9GAv9s"]
               (map #(gdoc/parse :html % false))
               (doall))))

(defn page [{:keys [slug title]} html]
  (html/refresh
    (str "pantry.swatow.co" (if slug (str "/" slug)))
    title
    {:styles ["/styles"
              "/fonts/fonts"
              "/fonts/1139"]}
    [:div#container
     [:div#header-container
      [:div#header
       [:a {:href "/"}
        [:img.logo {:src "/img/swatow-square.png" :alt "Swatow.co"}]]]]
     html]))

(defn header [{:keys [slug title lede date]}]
  [:a {:href (str "/" slug)}
   [:h3 title]
   [:h6
    [:strong lede]
    " "
    [:span.date "— " (:string date)]]
   [:div.sketch
    [:img {:src (format "/%s/sketch.png" slug)}]]])

(defn frontpage [posts]
  (page
    {:title "Swatow.co"}
    [:div.posts-container
     [:div.posts
      [:ul
       (for [post posts]
         [:li.post-container
          [:div.post-container-inner
           [:a {:href (str "/" (:slug post))}
            (header post)]]])]]]))

(defn post [p]
  (puget/cprint p)
  (page
    p
    [:div.post-container
     [:div.post-container-inner
      [:div.post
       [:div.post-header
        (header p)]
       [:div.content
        (:html p)]]]]))

(get-all-posts)

(do
  (frontpage @all-posts)
  (doall (map post @all-posts)))