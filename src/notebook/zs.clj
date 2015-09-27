(ns notebook.zs
  (:require [clojure.string :as string]
            [puget.printer :refer [cprint]]
            [notebook.gdoc :as gdoc]
            [hiccup.core :as html]
            [garden.core :as garden]))

(def essay "14ANhea-S4bz9GOOOPIwCqfhKyGFpWQ2oQHtW1K-kHuQ")

(defn save-essay []
  (let [html (gdoc/as-hiero-html essay)]
    (spit "wedding.html" (:html html))))

(defn print-essay []
  (let [content (slurp "wedding.html")]
    (spit "index.html" content)))