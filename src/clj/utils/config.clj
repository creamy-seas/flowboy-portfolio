(ns utils.config
  (:require
   [clojure.edn     :as edn]
   [clojure.java.io :as io]
   [utils.date      :as date]))

(defonce config
  ; Reading and exposing of config from resources/config.edn
  (let [base (-> "config.edn"
                 io/resource
                 slurp
                 edn/read-string)]
    (assoc base
       :age (:years (date/calculate-age (:bday base)))
       :season (date/current-season))))
