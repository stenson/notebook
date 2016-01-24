(ns notebook.swatow.pantry
  (:require [notebook.html :as html]
            [notebook.gdoc :as gdoc]
            [puget.printer :as puget]))

(def all-posts (atom []))

(defn get-all-posts []
  (reset! all-posts
          (->> ["1uXDM4acSAxXTcYtwqoSmZK7XDxIWP7l40Ne6t9GAv9s"]
               (map #(gdoc/parse :txt % false))
               (doall))))

(get-all-posts)

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
       [:img.logo {:src "/img/swatow-square.png" :alt "Swatow.co"}]]]
     html]))

(defn post [p]
  (puget/cprint (dissoc p :html))
  (page
    p
    [:div.post-container
     [:div.post-container-inner
      [:div.post
       [:h1 (:title p)]
       [:div.content
        (:html p)]]]]))

(do
  (page
    {:title "Swatow.co"}
    [:div.posts-container
     [:div.posts
      [:ul
       (for [{:keys [slug title lede]} @all-posts]
         [:li.post-container
          [:div.post-container-inner
           [:a {:href (str "/" slug)}
            [:h3 title]
            [:h6 lede]]]])]]])
  (doall (map post @all-posts)))