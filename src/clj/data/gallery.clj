(ns data.gallery
  (:require [data.core :as core]
            [utils.date   :as date]
            [utils.url    :as url]
            [utils.config :as cfg]))

(defn read-gallery
  "Reading of raw gallery data from csv sorted by descending dates
  - Each enty has both a thumbnail generated (for videos a default icon is used)
  - Each enty has a url made that will be fetched if clicked"
  []
  (let [favourites (:favourites cfg/config)]
    (->> (core/read-csv "data/gallery.csv")
         (sort-by :date #(compare %2 %1))
         (map-indexed
          (fn [index {:keys [id type description date] :as entry}]
            (assoc entry
                   :gallery-idx index
                   :thumbnail (if (= type "image")
                                (str "https://drive.google.com/thumbnail?id=" id)
                                (if (some #{description} favourites)
                                  (url/put-on-base "/assets/play-icon-favourite.svg")
                                  (url/put-on-base "/assets/play-icon.svg")))
                   :src    (str "https://drive.google.com/file/d/"
                                id
                                "/preview")
                   :age (date/calculate-age
                         (:bday cfg/config) date)))))))

(defn group-gallery
  "Return the gallery entires grouped by season descending"
  [gallery-data]
  (into (sorted-map-by (fn [a b] (compare b a)))
        (group-by :season gallery-data)))
