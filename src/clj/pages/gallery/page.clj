(ns pages.gallery.page
  (:require
   [hiccup.page                    :refer [include-js]]
   [common.layout                  :as layout]
   [utils.data.gallery             :as gallery]
   [utils.data.core                :as data]
   [utils.url                      :as url]
   [utils.config                   :as cfg]
   [pages.gallery.modal            :as modal]
   [pages.gallery.gallery-grid     :as grid]))

(defn render []
  (let [gallery-data (gallery/read-gallery)]
    (layout/main
     {:title (:title-tag-gallery cfg/config)
      :description "A showcase of fun and action moments"
      :extra-elements [(data/export-data gallery-data "GALLERY_DATA")
                       (data/export-data (dec (count gallery-data)) "GALLERY_DATA_MAX_IDX")]}
     (grid/render gallery-data)
     (modal/render)
     (include-js (url/put-on-base "/js/cljs_base.js")
                 (url/put-on-base "/js/gallery.js")))))
