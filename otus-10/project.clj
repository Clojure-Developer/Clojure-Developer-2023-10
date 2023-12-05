(defproject otus-10 "0.1.0-SNAPSHOT"
  :description "https://github.com/Clojure-Developer/Clojure-Developer-2023-10"
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :repl-options {:init-ns otus-10.core}
  :main ^:skip-aot otus-10.homework
  :target-path "target/%s"
  :profiles {:dev {}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})

