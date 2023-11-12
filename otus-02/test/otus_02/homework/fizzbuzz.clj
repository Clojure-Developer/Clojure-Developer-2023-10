(ns otus-02.homework.fizzbuzz
  (:require
   [clojure.test :refer :all]))

(defn fizz-buzz [n]
  (map
   #(cond
      (true? (and (= 0 (mod % 3)) (= 0 (mod % 5)))) "FizzBuzz"
      (= 0 (mod % 3)) "Fizz"
      (= 0 (mod % 5)) "Buzz"
      :else %) (range 1 (+ n 1) 1)))

(deftest fizz-buzz-test
  (is (= (fizz-buzz 10)
         '(1 2 "Fizz" 4 "Buzz" "Fizz" 7 8 "Fizz" "Buzz"))))
