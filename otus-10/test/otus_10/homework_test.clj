(ns otus-10-homework-test
  (:require [clojure.test :refer :all]
            [otus-10-homework :as sut]))

(deftest test-parse-mp3-tags
  (testing "проверка бита в байте, представленным числом"
    (is (false?  (sut/check-flag 1 5)))
  (testing "проверка размера фрейма"
    (is (= 129.0 (sut/get-size [0 0 1 1]))))
  (testing "проверка парсинга массива байтов с выбранной кодировкой"
    (is (= "Clojure" (sut/parse-frame-text [0 67 108 111 106 117 114 101]))))))
