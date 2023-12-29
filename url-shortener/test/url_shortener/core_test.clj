(ns url-shortener.core-test
  (:require [clojure.test :refer :all]
            [url-shortener.core :refer :all])
  (:import (java.util IllegalFormatException)))

(deftest test-positive-get-symbol-by-idx
  (is (= \a (get-symbol-by-idx 0)))
  (is (= \z (get-symbol-by-idx 25)))
  (is (= \A (get-symbol-by-idx 26)))
  (is (= \9 (get-symbol-by-idx 61)))
  (is (= \a (get-symbol-by-idx 62))))

(deftest test-negative-get-symbol-by-idx
  (is (= nil (get-symbol-by-idx -1))))

(deftest test-positive-get-idx
  (is (= 1.0 (get-idx 62)))
  (is (= 0.0 (get-idx 61)))
  (is (= 1.0 (get-idx 63))))

(deftest test-negative-get-idx+get-symbol-by-idx
  (testing "No abs"
    (is (= \b (get-symbol-by-idx (get-idx -62))))))

(deftest test-positive-id->url+url-id
  (testing "url->id"
    (is (= 12345 (url->id "dnh")))
    (is (= 3294233727 (url->id "dK6qQd"))))
  (testing "id->url"
    (is (= "dnh" (id->url 12345)))
    (is (= "dK6qQd" (id->url 3294233727))))
  (testing "chain test"
    (is (= "dnh" (id->url (url->id "dnh"))))
    (is (= 3294233727 (url->id (id->url 3294233727))))))

(deftest test-negative-id->url
  (testing "Negative id's -> blank string"
    (testing "Boundary test"
      (is ((complement clojure.string/blank?) (id->url -1))))
    (is ((complement clojure.string/blank?) (id->url -123)))))

(deftest test-negative-url->id
  (testing "NPE but IllegalFormatException expected"
    (is (thrown? IllegalFormatException (url->id ")")))))


