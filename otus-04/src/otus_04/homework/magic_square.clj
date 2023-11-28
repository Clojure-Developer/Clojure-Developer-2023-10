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
    (loop [acc (vec (repeat n (vec (repeat n 0))))
           i 0
           j (quot n 2)
           cnt 1]
        (if (= cnt (inc (* n n)))
            acc
            (let [new-acc (assoc acc i (assoc (get acc i) j cnt))
                  diag-step [(mod (dec i) n) (mod (inc j) n)]
                  down-step [(mod (inc i) n) j]
                  [i j] (if (zero? (get-in acc diag-step))
                            diag-step
                            down-step)]
                (recur new-acc i j (inc cnt))))))

(comment
    (magic-square 3)
    (magic-square 5)
    (doseq [a (magic-square 5)]
        (println a))
    (vec (repeat 3 (vec (repeat 3 0))))
    (vec [1 2 3])
    (let [[i j] [(mod (inc 2) 3)
                 (mod (dec 0) 3)]]
        (pr i j))
    (assoc (vec (repeat 3 (vec (repeat 3 0)))) 0 (assoc (get (vec (repeat 3 (vec (repeat 3 0)))) 0) 1 1))
    (get 1 (vec (repeat 3 (vec (repeat 3 0)))))
    (tr/dotrace [magic-square] (magic-square 1)))

