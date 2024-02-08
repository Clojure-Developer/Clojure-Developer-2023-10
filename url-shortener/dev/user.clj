(ns user
  (:import [org.slf4j.simple SimpleLogger]))

(System/setProperty SimpleLogger/DEFAULT_LOG_LEVEL_KEY "warn")

(defonce *server (atom nil))

(require '[url-shortener.main :as main])

(defn start-server []
  (reset! *server (main/start-server {:port 8000 :join? false})))

(defn stop-server []
  (when @*server
    (main/stop-server @*server)
    (reset! *server nil)))
