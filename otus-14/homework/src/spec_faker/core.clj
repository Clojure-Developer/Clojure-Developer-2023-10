(ns spec-faker.core
    (:require [cheshire.core :as cheshire]
              [clojure.spec.gen.alpha :as gen]
              [compojure.core :refer :all]
              [hiccup.core :refer [html]]
              [ring.adapter.jetty :refer [run-jetty]]
              [ring.middleware.json :refer [wrap-json-response]]
              [ring.middleware.keyword-params :refer :all]
              [ring.middleware.params :refer :all]
              [ring.middleware.resource :refer :all]
              [ring.util.response :refer [redirect resource-response response]]
              [spec-faker.generators :refer [generators]])
    (:gen-class))

(def example-input (cheshire/encode {:key1 "spec-string"
                                     :key2 "spec-int64"
                                     :key3 "spec-bool"}))

(defn generate-spec [json-string]
    (let [json (cheshire/decode json-string)]
        (response (zipmap (keys json)
                          (map (comp gen/generate generators) (vals json))))))

(defmulti spec-get-handler #(nil? (get-in % [:params :spec])))

(defmethod spec-get-handler true [_]
    (html [:form {:action "/" :method "POST"}
           [:div
            [:label {:for "spec"} "Input spec: "]
            [:input#spec {:name  "spec"
                          :value example-input
                          :style "width:500px; height:200px"}]
            ]
           [:button.btn.btn-primary {:type "submit"} "Submit"]]))

(defmethod spec-get-handler false [request]
    (generate-spec (get-in request [:params :spec])))

(defroutes handler
           (GET "/" request (spec-get-handler request))
           (POST "/" request (redirect (str "?spec=" (get-in request [:params :spec]))))
           (GET "/favicon.ico" _ (resource-response "public/favicon.ico")))

(def app (-> handler
             (wrap-resource "public")
             wrap-json-response
             wrap-keyword-params
             wrap-params
             ))

(def server (run-jetty #'app {:port 8000 :join? false}))
(comment
    (.stop server))
