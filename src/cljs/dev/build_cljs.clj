(ns dev.build-cljs
  (:require [cljs.build.api :as cljs]))

(def ^:private bundles
  "Define all the namespaces to builds into js files here
  e.g. :example 'gallery-build tells to evaluate the gallery.build and put it into example.js"
  {:gallery 'gallery.build
   :landing 'landing.build})

(def ^:private paths
  "For production take artifacts out of resources/public/js"
  {:src-dirs "src/cljs"
   :output-to "resources/public/js"
   :out-dirs {:advanced "target/cljs"
              :simple  "resources/public/js"}})

(defn ^:private out-dir
  "Returns output directory for given optimization level"
  [optimizations]
  (get-in paths [:out-dirs optimizations]))

(def ^:private modules-map
  "Make a module for each of the bundles, specifing namespace to evaluate
  and where to write the output"
  (let [eval-path (fn [bundle-name] (str (:output-to paths) "/" bundle-name ".js"))]
    (-> (into {}
              (map (fn [[bundle entry-ns]]
                     [bundle {:entries #{entry-ns}
                              :output-to (eval-path (name bundle))}])
                   bundles))
        (assoc :cljs-base {:output-to (eval-path "cljs_base")}))))

(defn build-config
  [optimizations]
  {;; optimizations
   :optimizations       optimizations
   :static-fns          true
   :optimize-constants  true
   :closure-defines     {"goog.DEBUG" false}
   :pprint              false
   :elide-asserts       true
   :pseudo-names        false
   ;; what to build and where
   :modules             modules-map
   :output-dir          (out-dir optimizations)
   ;; runtime
   :parallel-build      true
   :verbose             true})

(defn -main
  "Pass in `prod` or `dev` build as argument"
  [& [mode]]
  (let [optimization  (if (= mode "prod") :advanced :simple)
        cfg    (build-config optimization)
        action (if (= optimization :advanced) cljs/build cljs/watch)]
    (println "▶ cljs compiling" optimization cfg)
    (action (:src-dirs paths) cfg)))
