(defproject url-shortener "0.1.0-SNAPSHOT"
  :description "URL shortener app"

  :dependencies [[org.clojure/clojure "1.11.1"]]

  :main ^:skip-aot url-shortener.core

  :uberjar-name "url-shortener.jar"

  :resource-paths ["resources"]

  :profiles {:uberjar {:aot :all}})
