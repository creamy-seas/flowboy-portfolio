(ns data.gallery
  (:require [data.core :as core]
            [utils.url       :as url]))

(defn read-gallery
  "Reading of raw gallery data from csv sorted by descending dates
  - Each enty has both a thumbnail generated (for videos a default icon is used)
  - Each enty has a url made that will be fetched if clicked"
  []
  (->> (core/read-csv "data/gallery.csv")
       (sort-by :date #(compare %2 %1))
       (map-indexed
        (fn [index {:keys [id type] :as entry}]
          (assoc entry
                 :gallery-idx index
                 :thumbnail (if (= type "image")
                              (str "https://drive.google.com/thumbnail?id=" id)
                              (url/put-on-base "/assets/play-icon.svg"))
                 :src    (str "https://drive.google.com/file/d/"
                              id
                              "/preview"))))))

(defn group-gallery
  "Return the gallery entires grouped by season descending"
  [gallery-data]
  (into (sorted-map-by (fn [a b] (compare b a)))
        (group-by :season gallery-data)))
