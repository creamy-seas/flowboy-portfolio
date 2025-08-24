(ns dev.build-clj
  (:require [pages.gallery.page]
            [pages.landing.page]
            [pages.error.page]
            [clojure.java.io :as io]))

(def pages
  {"resources/public/index.html"         pages.landing.page/render
   "resources/public/404.html"           pages.error.page/render
   "resources/public/gallery/index.html" pages.gallery.page/render})

(defn- ensure-parent!
  "Make sure the parent directory of file-path exists."
  [^String file-path]
  (let [parent (.getParentFile (io/file file-path))]
    (when-not (.exists parent)
      (.mkdirs parent))))

(defn build-all!
  []
  (doseq [[out-path render-fn] pages]
    (ensure-parent! out-path)
    (spit out-path (render-fn))
    (println "✔" out-path "generated")))

(defn -main
  "Build all static pages."
  [& _]
  (println "▶ clj compiling")
  (build-all!))
