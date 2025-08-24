(ns pages.landing.gallery-link
  (:require [utils.url :as url]))

(defn render
  "Link to gallery page - nice, fat, and center of page"
  []
  [:section.text-center.p-6 [:a {:href (url/put-on-base "/gallery")
                                 :class "text-mytheme text-3xl font-bold
                                         underline underline-offset-4
                                         hover:text-mytheme/80"}
                             "View Gallery"]])
