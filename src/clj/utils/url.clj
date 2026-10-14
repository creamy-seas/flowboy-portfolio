(ns utils.url
  (:require [clojure.string :as str]
            [utils.config :as cfg]))

(defn put-on-base
  "Return a public URL for any local asset"
  [path]
  (if (or (re-find #"^(https?:)?//" path)
          (str/starts-with? path "/"))
    path
    (str "/" path)))

(defn absolute-url
  "Return an absolute URL for SEO and social media metadata."
  [path]
  (let [path (put-on-base path)]
    (if (re-find #"^https?://" path)
      path
      (str (:domain cfg/config) path))))
