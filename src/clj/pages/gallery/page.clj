(ns pages.gallery.page
  (:require
   [hiccup.page                    :refer [include-js]]
   [common.layout                  :as layout]
   [data.gallery                   :as gallery]
   [data.core                      :as core]
   [utils.url                      :as url]
   [utils.config                   :as cfg]
   [pages.gallery.modal            :as modal]
   [pages.gallery.gallery-grid     :as grid]))

(defn render []
  (let [gallery-data (gallery/read-gallery)]
    (layout/main
     {:title (:title-tag-gallery cfg/config)
      :description "A showcase of fun and action moments"
      :extra-elements [(core/export-data gallery-data "GALLERY_DATA")
                       (core/export-data (dec (count gallery-data)) "GALLERY_DATA_MAX_IDX")]}
     (grid/render gallery-data)
     (modal/render)
     (include-js (url/put-on-base "/js/cljs_base.js")
                 (url/put-on-base "/js/gallery.js")))))
