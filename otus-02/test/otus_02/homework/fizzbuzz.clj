(ns otus-02.homework.fizzbuzz
  (:require
   [clojure.test :refer :all]
   [clojure.math :as math]))

; "Создайте программу, которая выводит числа от 1 до n.
  ;  - Если число делится на 3, выведите 'Fizz';
  ;  - если число делится на 5, выведите 'Buzz';
  ;  - если число делится и на 3 и на 5, выведите 'FizzBuzz'."
  ; "implement me"

(defn fizz-buzz [n]
  (let [result
        (cond
          (= (rem n 3) 0) "Fizz"
          (= (rem n 5) 0) "Buzz"
          :else "FizzBuzz")]
    (println result)
    result))

(deftest fizz-buzz-test
  (is (= (fizz-buzz 10)
         "Buzz")))
