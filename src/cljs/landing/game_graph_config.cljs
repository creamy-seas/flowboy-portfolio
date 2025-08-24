(ns landing.game-graph-config)

(defn make-line
  "Line-specific formatting configuration and seeding of data"
  [game-stats label key color]
  {:label label
   :data  (mapv key game-stats)
   ;; line -------------
   :borderColor color
   ;; points -------------
   :pointBorderColor     color
   :pointHoverBackgroundColor color
   :pointHitRadius      20})

(defn make-line-collection [game-stats]
  [(make-line game-stats "Goals"     :goals     "#eec900")
   (make-line game-stats "Shots"     :shots     "#ff7300")
   (make-line game-stats "Passes"    :passes    "#00bfff")
   (make-line game-stats "Carries"   :carries   "#82ca9d")
   (make-line game-stats "Takeaways" :takeaways "#ff1493")])

(defn get-legend-title [items game-stats]
  (when-let [item (aget items 0)]
    (let [idx   (js/parseInt (aget item "dataIndex"))
          game  (nth (game-stats) idx)
          date  (:date       game)
          loc   (:location   game)
          name  (:name       game)]
      (str date " @" loc "\n" name))))

(defn get-axes-format [label]
  {:grid    {:color "rgba(128,128,128,0.20)" :lineWidth 0.5}
   :border  {:display true :width 2 :color "#696969"}
   :ticks   {:font {:size 18}}
   :title   {:display true :text label :font {:size 20}}})

(defn chart-opts [game-stats]
  {:responsive true
   :maintainAspectRatio false
   :interaction {:mode "index" :axis "x" :intersect true}

   ;; Common point params - line spefic ones defined in `make-line`
   :elements
   {:line   {:tension               0.2
             :fill                  false}

    :point  {:pointBackgroundColor  "#ffffff"
             :pointBorderWidth      2
             :pointRadius           3
             :pointHoverBorderColor "#ffffff"
             :pointHoverBorderWidth 3
             :pointHoverRadius      8}}

   :plugins
   {:legend {:position      "top"
             :labels        {:font {:size 20}
                             :boxHeight 1
                             :boxWidth 20}}
    :tooltip {:boxHeight 1
              :boxWidth 15
              :boxPadding 10
              :bodyFont  {:size 18}
              :callbacks {:title (fn [items]
                                   (get-legend-title items game-stats))}}}

   :scales
   {:x (get-axes-format "Game #")
    :y (get-axes-format "Count")}})
