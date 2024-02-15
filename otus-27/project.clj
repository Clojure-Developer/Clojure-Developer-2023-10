(defproject otus-27 "0.1.0-SNAPSHOT"
  :description "Lesson 27: Working with databases"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.seancorfield/next.jdbc "1.3.909"]
                 [com.h2database/h2 "2.2.224"]
                 [com.github.seancorfield/honeysql "2.5.1103"]]
  :repl-options {:init-ns otus-27.core})
