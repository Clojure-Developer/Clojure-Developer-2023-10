(defproject spec-faker "0.1.0-SNAPSHOT"
  :description "A simple Web-app that can fake for you some data using your Swagger spec"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/test.check "0.9.0"]
                 [ring/ring-core "1.10.0"]
                 [ring/ring-jetty-adapter "1.10.0"]
                 [ring/ring-defaults "0.3.4"]
                 [compojure "1.7.0"]
                 [org.clojure/data.json "2.5.0"]
                 [hiccup "1.0.5"]
                 [cheshire "5.11.0"]
                 [ring/ring-json "0.5.1"]]
  :main ^:skip-aot spec-faker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
