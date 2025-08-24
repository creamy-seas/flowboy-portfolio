(ns utils.url
  (:require [clojure.string :as str]
            [utils.config   :as cfg]))

(defn put-on-base
  "Return a public URL for any local asset"
  [path]
  (cond
    (re-find #"^(https?:)?//" path)  path
    (str/starts-with? path "/")         (str (:base cfg/config) path)
    :else                               (str (:base cfg/config) "/" path)))
