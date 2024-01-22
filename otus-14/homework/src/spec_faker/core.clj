(ns spec-faker.core
    (:require [clojure.spec.gen.alpha :as gen]
              [compojure.core :refer :all]
              [ring.adapter.jetty :refer [run-jetty]]
              [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
              [ring.util.response :refer [response]]
              [spec-faker.generators :refer [generators]])
    (:gen-class))

(comment
    {:key1 "spec-string"
     :key2 "spec-int64"
     :key3 "spec-bool"})
(def current-spec (atom {}))

(defn change-spec [request]
    (reset! current-spec (request :body))
    (response "OK"))

(defn generate-spec []
    (response (zipmap (keys @current-spec)
                (map (comp gen/generate generators)
                     (vals @current-spec)))))

(defroutes handler
           (context "/spec" []
               (GET "/" _ (generate-spec))
               (POST "/" request (change-spec request))))
(defn wrap-print-response [handler]
    (fn
        ([request]
         (print request)
         (handler request))))
(def app (-> handler
             wrap-json-body
             wrap-json-response
             ;wrap-print-response
             ))

(def server (run-jetty #'app {:port 8000 :join? false}))
(comment
    (.stop server))
