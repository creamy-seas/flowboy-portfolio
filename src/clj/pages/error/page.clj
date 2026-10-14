(ns pages.error.page
  (:require
   [utils.url       :as url]
   [utils.config    :as cfg]
   [common.layout   :as layout]
   [common.elements :as common]))

(defn render
  "404 with a funny dude and redirect"
  []
  (layout/main
   {:title       (:title-tag-404 cfg/config)
    :description "Nothing here!"}
   [:container.space-y-8.text-center.flex.flex-col.justify-center
    [:div.relative.flex.items-center.justify-center
     [:div
      {:class (str "absolute inset-0 rounded-lg filter blur-2xl"
                   " bg-gradient-to-br from-mytheme " (:error-bg cfg/config) " to-transparent")}]
     [:img
      {:src   (url/put-on-base "/assets/favicon.svg")
       :alt   "Let's go!"
       :class "relative z-10
               w-[40%] w-min-[200px] h-auto mb-4"}]]
    (common/fat-title "404: Sorry, nothing here")]))
