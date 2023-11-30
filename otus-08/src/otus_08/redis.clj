(ns otus-08.redis
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is run-tests]]))

(defn handler [command]
  (reply (handle command)))


(deftest basic
  (is (= "+PONG\r\n" (handler ["PING"])))
  (is (= "+Hello\r\n" (handler ["ECHO" "Hello"]))))

(deftest get-and-set
  (is (= "+(nil)\r\n" (handler ["GET" "nonexistent"])))
  (is (= "+OK\r\n" (handler ["SET" "x" "42"]))))

(deftest command-case
  (is (= "+Hello\r\n" (handler ["echo" "Hello"]))))

(comment
  (run-tests 'otus-08.redis)
  
  (ns-unmap *ns* 'handle))
