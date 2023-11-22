(ns otus-04.homework.magic-square)

;; Оригинальная задача:
;; https://www.codewars.com/kata/570b69d96731d4cf9c001597
;;
;; Подсказка: используйте "Siamese method"
;; https://en.wikipedia.org/wiki/Siamese_method

(defn fill-matrix [n max curr x y matrix]
  "next"
  (if (> curr max) matrix
      (let [new-matrix (assoc-in matrix [x y] curr)
            new-x (if (zero? x) (dec n) (dec x))
            new-y (if (= y (dec n)) 0 (inc y))
            nnew-x (if (zero? (get-in new-matrix [new-x new-y])) new-x (inc x))
            nnew-y (if (= new-x nnew-x) new-y y)]
        (println curr x y)
        (println new-matrix)
        (fill-matrix n max (inc curr) nnew-x nnew-y new-matrix))))

(defn magic-square
  "Функция возвращает вектор векторов целых чисел,
  описывающий магический квадрат размера n*n,
  где n - нечётное натуральное число.

  Магический квадрат должен быть заполнен так, что суммы всех вертикалей,
  горизонталей и диагоналей длиной в n должны быть одинаковы."
  [n]
  (fill-matrix n (* n n) 1 0 (int  (/ (dec n) 2)) (vec (repeat n (vec (repeat n 0))))))


(comment
  (magic-square 3)
  (magic-square 1)
  (magic-square 5)
  (fill-matrix 3 9 1 0 1 [[0 0 0] [0 0 0] [0 0 0]])
  (assoc-in [[0 0 0] [0 0 0] [0 0 0]]  [1 1] 5))

