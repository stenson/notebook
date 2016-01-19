(ns notebook.swatow.pantry
  (:require [notebook.html :as html]
            [notebook.gdoc :as gdoc]
            [puget.printer :as puget]))

(def all-posts
  (->> ["1uXDM4acSAxXTcYtwqoSmZK7XDxIWP7l40Ne6t9GAv9s"]
       (map #(gdoc/parse :txt % false))
       (doall)))

(defn page [{:keys [slug title]} html]
  (html/refresh
    (str "pantry.swatow.co" (if slug (str "/" slug)))
    title
    {:styles ["/styles"]}
    [:div#container
     [:div#header-container
      [:div#header
       [:h1 "The Swatow Pantry"]]]
     html]))

(defn post [p]
  (puget/cprint (dissoc p :html))
  (page
    p
    [:div.post-container
     [:div.post
      (:html p)]]))

(do
  (page
    {:title "The Swatow Pantry"}
    [:div.posts-container
     [:div.posts
      [:ul
       (for [{:keys [slug title lede]} all-posts]
         [:li.post-link
          [:a {:href (str "/" slug)}
           [:h3 title]
           [:h5 lede]]])]]])
  (doall (map post all-posts)))

#_(html/refresh
  "pantry.swatow.co"
  "The Swatow Pantry"
  {:styles ["styles"]}
  [:div#container
   [:div#header-container
    [:div#header
     [:h1 "The Swatow Pantry"]]]
   [:div.post-container
    [:div.post
     (:html dried-honey-dates)]]])