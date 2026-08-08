(ns dev.server
  (:require
   [ring.adapter.jetty        :refer [run-jetty]]
   [ring.middleware.file      :refer [wrap-file]]
   [ring.middleware.file-info :refer [wrap-file-info]]
   [ring.util.response        :as resp]
   [ring.middleware.not-modified :refer [wrap-not-modified]]))

(def static-handler
  (-> (constantly nil)
      (wrap-file "resources/public")
      ^{:clj-kondo/ignore [:deprecated-var]} wrap-file-info
      wrap-not-modified))

(defn app [{:keys [uri] :as req}]
  (or (static-handler
       (assoc req :uri (if (= uri "/") "/index.html" uri)))
      (resp/redirect "/")))

(defn -main [& _]
  (run-jetty app {:port 5173 :join? true}))
