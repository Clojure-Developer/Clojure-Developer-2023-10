(ns otus-02.homework.fizzbuzz
  (:require
   [clojure.test :refer :all]))


(defn fizz-buzz [n]
  " Программа, которая выводит числа от 1 до n.
- Если число делится на 3, выведите 'Fizz';
- если число делится на 5, выведите 'Buzz';
- если число делится и на 3 и на 5, выведите 'FizzBuzz'. "
  (map (fn [x]
         (cond
           (and (zero? (rem x 3)) (zero? (rem x 5))) "FizzBuzz"
           (zero? (rem x 3)) "Fizz"
           (zero? (rem x 5)) "Buzz"
           :else x))
       (range 1 (inc n))))


(deftest fizz-buzz-test
  (is (= (fizz-buzz 10)
         '(1 2 "Fizz" 4 "Buzz" "Fizz" 7 8 "Fizz" "Buzz"))))
