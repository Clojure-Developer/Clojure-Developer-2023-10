(ns otus-02.homework.fizzbuzz
  (:require
   [clojure.test :refer :all]))


(defn div? [n d]
    (zero? (mod n d)))

(defn cond-div [a]
    (cond
        (div? a 15) (str "FizzBuzz")
        (div? a 5) (str "Buzz")
        (div? a 3) (str "Fizz")
        :else a))

(defn fizz-buzz [n]
    (map cond-div (range 1 (inc n))))


(deftest fizz-buzz-test
  (is (= (fizz-buzz 10)
         '(1 2 "Fizz" 4 "Buzz" "Fizz" 7 8 "Fizz" "Buzz"))))
