(ns otus-04.homework.magic-square)

;; Оригинальная задача:
;; https://www.codewars.com/kata/570b69d96731d4cf9c001597
;;
;; Подсказка: используйте "Siamese method"
;; https://en.wikipedia.org/wiki/Siamese_method


  "Функция возвращает вектор векторов целых чисел,
  описывающий магический квадрат размера n*n,
  где n - нечётное натуральное число.

  Магический квадрат должен быть заполнен так, что суммы всех вертикалей,
  горизонталей и диагоналей длиной в n должны быть одинаковы."

;; r = 1..n  MagSq = n * mod(r' + (r - div(n + 3, 2)), n) + mod(r' + r * 2 - 2, n) + 1

(defn magic-square [n]
  {:pre [(odd? n)]}
  (let [rng          (->> n 
                          range 
                          (map inc))
        repeat-rng   (fn [x] (flatten (repeat n x)))
        sum-row-col  (fn [x y] (map + (sort (repeat-rng x)) (repeat-rng y)))
        f-main       (fn [x] (map #(mod % n) (sum-row-col rng x)))
        b1           (map (partial + (- (quot (+ n 3), 2))) rng)
        b2           (map (partial #(- (* 2 %) 2)) rng)]
    (->> (map + (map (partial * n) (f-main b1)) (f-main b2))
         (map inc)
         (partition n)
         (map vec)
         vec)))
