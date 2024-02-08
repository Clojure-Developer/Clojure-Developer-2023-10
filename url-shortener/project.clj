(defproject url-shortener "0.1.0-SNAPSHOT"
  :description "URL shortener app"

  :source-paths ["src" "resources"]
  
  :dependencies [;; Backend
                 [org.clojure/clojure "1.11.1"]
                 [ring/ring-jetty-adapter "1.11.0"]
                 [ring/ring-json "0.5.1"]
                 [compojure "1.7.1"]
                 
                 ;; Frontend
                 [reagent "1.2.0"]
                 [org.clojure/core.async "1.6.681"]
                 [cljs-http "0.1.48"
                  :exclusions [org.clojure/core.async
                               com.cognitect/transit-cljs
                               com.cognitect/transit-js]]
                 [thheller/shadow-cljs "2.27.2"] ; Keep it synced with npm version!
                 ]

  :main ^:skip-aot url-shortener.core

  :uberjar-name "url-shortener.jar"

  :resource-paths ["resources"]

  :profiles {:dev {:dependencies [[ring/ring-devel "1.11.0"]]}
             :uberjar {:aot :all}})
