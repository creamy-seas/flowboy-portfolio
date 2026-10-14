(ns data.gallery
  (:require [data.core :as core]
            [utils.date   :as date]
            [utils.url    :as url]
            [utils.config :as cfg]))

(defn gallery-media
  "Define how to generate media links and embedding for different providers"
  [id type]
  (case (:gallery-provider cfg/config)
    :google-drive {:src (str "https://drive.google.com/file/d/" id "/preview")
                   :embed "iframe"}
    :cdn          {:src (str (:cdn-url cfg/config) id)
                   :embed type}))

(defn gallery-thumbnail
  "Define how to generate media preview in gallery grid"
  [id]
  (case (:gallery-provider cfg/config)
    :google-drive (str "https://drive.google.com/thumbnail?id=" id)
    :cdn          (str (:cdn-url cfg/config) id)))

(defn read-gallery
  "Reading of raw gallery data from csv sorted by descending dates
  - Each enty has both a thumbnail generated (for videos a default icon is used)
  - Each enty has a url made that will be fetched if clicked
  - Entry may have an optional instagram link which is also stored"
  []
  (let [favourites (:favourites cfg/config)
        instagram-links
        (->> (core/read-csv "data/instagram.csv")
             (map (juxt :gallery-description :instagram-link))
             (into {}))]
    (->> (core/read-csv "data/gallery.csv")
         (sort-by :date)
         (map-indexed
          (fn [index {:keys [id type description date] :as entry}]
            (assoc entry
                   :gallery-idx index
                   :thumbnail (if (= type "image")
                                ;; TODO: for now we just use the image itself - it is heavy as all images will be loaded, so its best to default to google drive
                                ;;(str "https://drive.google.com/thumbnail?id=" id)
                                (gallery-thumbnail id)
                                (if (some #{description} favourites)
                                  (url/put-on-base "/assets/play-icon-favourite.svg")
                                  (url/put-on-base "/assets/play-icon.svg")))
                   :src (:src (gallery-media id type))
                   :embed (:embed (gallery-media id type))
                   :instagram (get instagram-links description)
                   :age (let [{:keys [years months]}
                              (date/calculate-age (:bday cfg/config) date)]
                          (format "%d years %d months" years months)))))
         (sort-by :date #(compare %2 %1)))))

(defn group-gallery
  "Return the gallery entires grouped by season descending"
  [gallery-data]
  (into (sorted-map-by (fn [a b] (compare b a)))
        (group-by :season gallery-data)))
