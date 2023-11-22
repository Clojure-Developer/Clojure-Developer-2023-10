(ns otus-04.homework.magic-square)

;; Оригинальная задача:
;; https://www.codewars.com/kata/570b69d96731d4cf9c001597
;;
;; Подсказка: используйте "Siamese method"
;; https://en.wikipedia.org/wiki/Siamese_method


;; r = 1..n  MagSq = n * mod(r' + (r - div(n + 3, 2)), n) + mod(r' + r * 2 - 2, n) + 1
(defn magic-square [n]
  {:pre [(odd? n)]}
  (let [rng          (map inc (range n))
        repeat-rng   (fn [x] (flatten (repeat n x)))
        sum-row-col  (fn [x y] (map + (sort (repeat-rng x)) (repeat-rng y)))
        f-main       (fn [x] (map #(mod % n) (sum-row-col rng x)))
        b1           (map #(- % (quot (+ n 3), 2)) rng)
        b2           (map #(- (* % 2) 2) rng)]
    (->> (map + (map #(* % n) (f-main b1)) (f-main b2))
         (map inc)
         (partition n)
         (map vec)
         vec)))
