(ns notebook.colors
  (:require [clojure.string :as string]
            [clojure.pprint :refer [cl-format]]))

(defn padn [n s]
  (cl-format nil (format "~%s,'0d" n) s))

(def html-colors
  (->> [["AliceBlue" "#F0F8FF"]
        ["AntiqueWhite" "#FAEBD7"]
        ["Aqua" "#00FFFF"]
        ["Aquamarine" "#7FFFD4"]
        ["Azure" "#F0FFFF"]
        ["Beige" "#F5F5DC"]
        ["Bisque" "#FFE4C4"]
        ["Black" "#000000"]
        ["BlanchedAlmond" "#FFEBCD"]
        ["Blue" "#0000FF"]
        ["BlueViolet" "#8A2BE2"]
        ["Brown" "#A52A2A"]
        ["BurlyWood" "#DEB887"]
        ["CadetBlue" "#5F9EA0"]
        ["Chartreuse" "#7FFF00"]
        ["Chocolate" "#D2691E"]
        ["Coral" "#FF7F50"]
        ["CornflowerBlue" "#6495ED"]
        ["Cornsilk" "#FFF8DC"]
        ["Crimson" "#DC143C"]
        ["Cyan" "#00FFFF"]
        ["DarkBlue" "#00008B"]
        ["DarkCyan" "#008B8B"]
        ["DarkGoldenrod" "#B8860B"]
        ["DarkGray" "#A9A9A9"]
        ["DarkGreen" "#006400"]
        ["DarkKhaki" "#BDB76B"]
        ["DarkMagenta" "#8B008B"]
        ["DarkOliveGreen" "#556B2F"]
        ["DarkOrange" "#FF8C00"]
        ["DarkOrchid" "#9932CC"]
        ["DarkRed" "#8B0000"]
        ["DarkSalmon" "#E9967A"]
        ["DarkSeaGreen" "#8FBC8F"]
        ["DarkSlateBlue" "#483D8B"]
        ["DarkSlateGray" "#2F4F4F"]
        ["DarkTurquoise" "#00CED1"]
        ["DarkViolet" "#9400D3"]
        ["DeepPink" "#FF1493"]
        ["DeepSkyBlue" "#00BFFF"]
        ["DimGray" "#696969"]
        ["DodgerBlue" "#1E90FF"]
        ["FireBrick" "#B22222"]
        ["FloralWhite" "#FFFAF0"]
        ["ForestGreen" "#228B22"]
        ["Fuchsia" "#FF00FF"]
        ["Gainsboro" "#DCDCDC"]
        ["GhostWhite" "#F8F8FF"]
        ["Gold" "#FFD700"]
        ["Goldenrod" "#DAA520"]
        ["Gray" "#808080"]
        ["Green" "#008000"]
        ["GreenYellow" "#ADFF2F"]
        ["Honeydew" "#F0FFF0"]
        ["HotPink" "#FF69B4"]
        ["IndianRed" "#CD5C5C"]
        ["Indigo" "#4B0082"]
        ["Ivory" "#FFFFF0"]
        ["Khaki" "#F0E68C"]
        ["Lavender" "#E6E6FA"]
        ["LavenderBlush" "#FFF0F5"]
        ["LawnGreen" "#7CFC00"]
        ["LemonChiffon" "#FFFACD"]
        ["LightBlue" "#ADD8E6"]
        ["LightCoral" "#F08080"]
        ["LightCyan" "#E0FFFF"]
        ["LightGoldenrodYellow" "#FAFAD2"]
        ["LightGreen" "#90EE90"]
        ["LightGrey" "#D3D3D3"]
        ["LightPink" "#FFB6C1"]
        ["LightSalmon" "#FFA07A"]
        ["LightSeaGreen" "#20B2AA"]
        ["LightSkyBlue" "#87CEFA"]
        ["LightSlateGray" "#778899"]
        ["LightSteelBlue" "#B0C4DE"]
        ["LightYellow" "#FFFFE0"]
        ["Lime" "#00FF00"]
        ["LimeGreen" "#32CD32"]
        ["Linen" "#FAF0E6"]
        ["Magenta" "#FF00FF"]
        ["Maroon" "#800000"]
        ["MediumAquamarine" "#66CDAA"]
        ["MediumBlue" "#0000CD"]
        ["MediumOrchid" "#BA55D3"]
        ["MediumPurple" "#9370DB"]
        ["MediumSeaGreen" "#3CB371"]
        ["MediumSlateBlue" "#7B68EE"]
        ["MediumSpringGreen" "#00FA9A"]
        ["MediumTurquoise" "#48D1CC"]
        ["MediumVioletRed" "#C71585"]
        ["MidnightBlue" "#191970"]
        ["MintCream" "#F5FFFA"]
        ["MistyRose" "#FFE4E1"]
        ["Moccasin" "#FFE4B5"]
        ["NavajoWhite" "#FFDEAD"]
        ["Navy" "#000080"]
        ["OldLace" "#FDF5E6"]
        ["Olive" "#808000"]
        ["OliveDrab" "#6B8E23"]
        ["Orange" "#FFA500"]
        ["OrangeRed" "#FF4500"]
        ["Orchid" "#DA70D6"]
        ["PaleGoldenrod" "#EEE8AA"]
        ["PaleGreen" "#98FB98"]
        ["PaleTurquoise" "#AFEEEE"]
        ["PaleVioletRed" "#DB7093"]
        ["PapayaWhip" "#FFEFD5"]
        ["PeachPuff" "#FFDAB9"]
        ["Peru" "#CD853F"]
        ["Pink" "#FFC0CB"]
        ["Plum" "#DDA0DD"]
        ["PowderBlue" "#B0E0E6"]
        ["Purple" "#800080"]
        ["Red" "#FF0000"]
        ["RosyBrown" "#BC8F8F"]
        ["RoyalBlue" "#4169E1"]
        ["SaddleBrown" "#8B4513"]
        ["Salmon" "#FA8072"]
        ["SandyBrown" "#F4A460"]
        ["SeaGreen" "#2E8B57"]
        ["Seashell" "#FFF5EE"]
        ["Sienna" "#A0522D"]
        ["Silver" "#C0C0C0"]
        ["SkyBlue" "#87CEEB"]
        ["SlateBlue" "#6A5ACD"]
        ["SlateGray" "#708090"]
        ["Snow" "#FFFAFA"]
        ["SpringGreen" "#00FF7F"]
        ["SteelBlue" "#4682B4"]
        ["Tan" "#D2B48C"]
        ["Teal" "#008080"]
        ["Thistle" "#D8BFD8"]
        ["Tomato" "#FF6347"]
        ["Turquoise" "#40E0D0"]
        ["Violet" "#EE82EE"]
        ["Wheat" "#F5DEB3"]
        ["White" "#FFFFFF"]
        ["WhiteSmoke" "#F5F5F5"]
        ["Yellow" "#FFFF00"]
        ["YellowGreen" "#9ACD32"]]
       (map (fn [[k v]] [(string/lower-case k) v]))
       (into {})))

(defn get-html-color-by-name [name]
  (get html-colors (string/lower-case name)))

(defn ck [key]
  (get-html-color-by-name (name key)))

(defn hex->rgb [hex]
  (->> (drop 1 (char-array hex))
       (partition 2)
       (map (partial apply str))
       (map #(Integer/parseInt % 16))))

(defn rgb->hex [rgb]
  (->> (map #(string/upper-case (Integer/toHexString %)) rgb)
       (map #(padn 2 %))
       (apply str "#")))

(defn clamppf [f] (max (min f 100.0) 0.0))

(defn hex->hsl [hex]
  (let [[r g b] (map #(/ % 255.0) (hex->rgb hex))
        min (min r g b)
        max (max r g b)
        delta (- max min)
        l (/ (+ max min) 2.0)
        h (condp = max
            min 0.0
            r (* 60 (/ (- g b) delta))
            g (+ 120 (* 60 (/ (- b r) delta)))
            b (+ 240 (* 60 (/ (- r g) delta))))
        s (cond
            (= max min) 0.0
            (< l 0.5) (/ delta (* 2 l))
            :else (/ delta (- 2 (* 2 l))))]
    {:h (mod h 360.0)
     :s (clamppf (* 100.0 s))
     :l (clamppf (* 100.0 l))}))

(defn hex->rgb-percents [hex]
  (map #(/ (float %) 255) (hex->rgb hex)))

(defn hue->rgb [m1 m2 hue]
  (let [h (cond
            (< hue 0) (inc hue)
            (> hue 1) (dec hue)
            :else hue)]
    (cond
      (< (* h 6) 1) (+ m1 (* (- m2 m1) h 6))
      (< (* h 2) 1) m2
      (< (* h 3) 2) (+ m1 (* (- m2 m1) (- (/ 2.0 3) h) 6))
      :else m1)))

(defn hsl->rgb [{hue :h sat :s lit :l}]
  (let [[h s l] [(/ hue 360.0) (/ sat 100.0) (/ lit 100.0)]
        m2 (if (<= l 0.5)
             (* l (+ s 1))
             (- (+ l s) (* l s)))
        m1 (- (* l 2) m2)]
    (into []
          (map #(Math/round (* 0xff %))
               [(hue->rgb m1 m2 (+ h (/ 1.0 3)))
                (hue->rgb m1 m2 h)
                (hue->rgb m1 m2 (- h (/ 1.0 3)))]))))

(defn mod-color
  ([which hex pct]
   (let [[key op] (case (keyword which)
                    :saturate [:s +]
                    :desaturate [:s -]
                    :lighten [:l +]
                    :darken [:l -])]
     (mod-color key op hex pct)))
  ([key op hex pct]
   (-> (update-in (hex->hsl hex) [key] #(clamppf (op % pct)))
       (hsl->rgb)
       (rgb->hex))))

(def sat (partial mod-color :saturate))
(def desat (partial mod-color :desaturate))
(def lighten (partial mod-color :lighten))
(def darken (partial mod-color :darken))

(defn random []
  (second (rand-nth (into [] html-colors))))

(defn str->color [val]
  (cond
    (sequential? val)
    (mod-color (nth val 0) (str->color (nth val 1)) (nth val 2))
    (string? val)
    (if (re-matches #"^#.*" val)
      val
      (get-html-color-by-name val))))