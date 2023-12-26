(ns server.charset
  (:require
   [ring.util.response :refer [charset]]))


(defn wrap-charset [handler]
  (fn [req]
    (charset (handler req) "UTF-8")))