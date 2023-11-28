(ns otus-04.homework.magic-square)

;; Оригинальная задача:
;; https://www.codewars.com/kata/570b69d96731d4cf9c001597
;;
;; Подсказка: используйте "Siamese method"
;; https://en.wikipedia.org/wiki/Siamese_method

(defn magic-square
  "Функция возвращает вектор векторов целых чисел,
  описывающий магический квадрат размера n*n,
  где n - нечётное натуральное число.

  Магический квадрат должен быть заполнен так, что суммы всех вертикалей,
  горизонталей и диагоналей длиной в n должны быть одинаковы."
  [n]
  (loop [acc (vec (repeat (inc n) (vec (repeat (inc n) 0))))
         i 0
         j (inc (quot n 2))
         cnt 1]
    (if (= cnt (* n n))
      acc
      (let [acc (assoc acc i (assoc (get i acc) j cnt))
            [i j] (if (zero? (get-in [i j] acc))
                    [(inc i) (dec j)]
                    [i (inc j)])]
        (recur acc i j (inc cnt))))))
