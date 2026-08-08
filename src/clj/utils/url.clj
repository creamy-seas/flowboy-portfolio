(ns utils.url
  (:require [clojure.string :as str]))

(defn put-on-base
  "Return a public URL for any local asset"
  [path]
  (if (or (re-find #"^(https?:)?//" path)
          (str/starts-with? path "/"))
    path
    (str "/" path)))
