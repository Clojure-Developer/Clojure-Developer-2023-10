(ns url-shortener.core-test
  (:require [clojure.test :refer :all]
            [url-shortener.core :as sut]))

;; В проде я бы навесил проверок на входящие аргументы в ф-ии url->id и id->url, 
;; т.к. они являются публичным апи

(deftest test-get-idx
  (testing "Legal arguments"
    (is (= 16.0 (sut/get-idx 1000)))
    (is (< 0 (sut/get-idx Long/MAX_VALUE))))
  (testing "Illegal arguments"
    (is (thrown? ClassCastException (sut/get-idx {:value 1000})))
    (is (thrown-with-msg? ClassCastException
                          #"to class java.lang.Number"
                          (sut/get-idx "1000")))))

(deftest test-get-symbol-by-idx
  (testing "Legal arguments"
    (are [result value] (= result (sut/get-symbol-by-idx value))
      \O 5000
      \k 10))
  (testing "Illegal arguments"
    (is (nil?  (sut/get-symbol-by-idx -10)))
    (is (thrown? ClassCastException  (sut/get-symbol-by-idx "10")))))

(deftest test-id->url
  (testing "Legal arguments"
    (is (= "dnh" (sut/id->url 12345)))
    (is (= "dK6qQd" (sut/id->url 3294233727))))
  (testing "Illegal arguments"
    (is (thrown? ClassCastException (sut/id->url "dnh")))))

(deftest test-url->id
  (testing "Legal arguments"
    (is (= 12345 (sut/url->id "dnh")))
    (is (= 3294233727 (sut/url->id "dK6qQd"))))
  (testing "Illegal arguments"
    (is (thrown? IllegalArgumentException (sut/url->id 12345)))))

(comment
  (run-tests))
