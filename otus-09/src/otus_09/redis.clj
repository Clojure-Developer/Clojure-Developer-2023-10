(ns otus-09.redis
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is run-tests]]))

(defn reply [s]
  (format "+%s\r\n" s))

(defn dispatch-fn [x]
  (-> x first str/upper-case))

(defmulti handle #'dispatch-fn)

(defmethod handle "PING"
  [_]
  "PONG")

(defmethod handle "ECHO"
  [[_ x]]
  x)

(defmethod handle "GET"
  [_]
  "(nil)")

(defmethod handle "SET"
  [_]
  "OK")

(defn handler [command]
  (reply (handle command)))

;; [CMD]
;; [CMD arg1 arg2]

(deftest basic
  (is (= "+PONG\r\n" (handler ["PING"])))
  (is (= "+Hello\r\n" (handler ["ECHO" "Hello"]))))

(deftest get-and-set
  (is (= "+(nil)\r\n" (handler ["GET" "nonexistent"])))
  (is (= "+OK\r\n" (handler ["SET" "x" "42"]))))

(deftest command-case
  (is (= "+Hello\r\n" (handler ["echo" "Hello"]))))

(comment
  (run-tests 'otus-09.redis)
  
  (ns-unmap *ns* 'handle))
