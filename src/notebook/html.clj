(ns notebook.html
  (:require [garden.core :as garden]
            [hiccup.page :refer [html5]]
            [me.raynes.fs :as fs]))

(defn favicon [size]
  [:link {:href  (format "favicon-%sx%s.png" size size)
          :sizes (format "%sx%s" size size)
          :rel   "icon"
          :type  "image/png"}])

(defn now []
  (quot (System/currentTimeMillis) 1000))

(defn favicons [& sizes]
  (->> (map favicon sizes)
       (conj [:link {:href "favicon.ico" :rel "icon" :type "image/x-icon"}])))

(defn inline-style [& styles]
  [:style {:type "text/css"}
   (garden/css {:pretty-print? false} styles)])

(defn style-link [href]
  [:link {:type "text/css"
          :href (format "%s.css?n=%s" href (now))
          :rel  "stylesheet"}])

(defn js-link [href]
  [:script {:type "text/javascript"
            :src  (format "%s.js?n=%s" href (now))}])

(defn inline-js [src]
  [:script {:type "text/javascript"} src])

(defn basic [title
             {:keys [styles
                     scripts
                     mobile-width
                     typekit
                     analytics
                     jquery]}
             content]
  (html5
    {:lang "en"}
    (list
      [:head
       [:meta {:charset "utf-8"}]
       (when mobile-width
         [:meta {:name "viewport"
                 :content (format "width=%s, initial-scale=1" mobile-width)}])
       [:title title]
       (list (favicons 16 32 96))
       (if (string? styles)
         (format [:style {:type "text/css"} styles])
         (list (map style-link styles)))
       (when jquery
         (case jquery
           1 [:script {:src "https://code.jquery.com/jquery-1.12.4.min.js"
                       :integrity "sha256-ZosEbRLbNQzLpnKIkEdrPv7lOy9C27hHQ+Xp8a4MxAQ="
                       :crossorigin "anonymous"}]
           3 [:script {:src "https://code.jquery.com/jquery-3.1.0.min.js"
                       :integrity "sha256-cCueBR6CsyA4/9szpPfrX3s49M9vUU5BgtiJj06wt/s="
                       :crossorigin "anonymous"}]))
       (when analytics
         [:script {}
          (format
            "(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');\n\n  ga('create', '%s', 'auto');\n  ga('send', 'pageview');"
            analytics)])
       (when typekit
         (list
           [:script {:type "text/javascript"
                     :src (format "https://use.typekit.net/%s.js" typekit)}]
           [:script {:type "text/javascript"}
            "try{Typekit.load({ async: true });}catch(e){}"]))]
      [:body
       (list
         content
         (list (map js-link scripts)))])))

(defn refresh [site title options content]
  (let [folder (format "sites/%s" site)]
    (fs/mkdirs folder)
    (spit
      (str folder "/index.html")
      (basic title options content))))