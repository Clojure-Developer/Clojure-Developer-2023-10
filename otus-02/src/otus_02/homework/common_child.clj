(ns otus-02.homework.common-child)

;; Строка называется потомком другой строки,
;; если она может быть образована путем удаления 0 или более символов из другой строки.
;; Буквы нельзя переставлять.
;; Имея две строки одинаковой длины, какую самую длинную строку можно построить так,
;; чтобы она была потомком обеих строк?

;; Например 'ABCD' и 'ABDC'

;; Эти строки имеют два дочерних элемента с максимальной длиной 3, ABC и ABD.
;; Их можно образовать, исключив D или C из обеих строк.
;; Ответ в данном случае - 3

;; Еще пример HARRY и SALLY. Ответ будет - 2, так как общий элемент у них AY

(defn create-matrix [x y]
  (vec (repeat y (vec (repeat x 0)))))

(defn get-value-setter [first-string second-string]
  (fn [i j matrix]
    (if (= (get first-string (dec i))
           (get second-string (dec j)))
      (+ (get-in matrix [(dec j) (dec i)] 0) 1)
      (max
       (get-in matrix [(dec j) i], 0)
       (get-in matrix [j (dec i)] 0)))))

(defn visit [matrix x y setter]
  (loop [i 1 j 1 matrix matrix]
    (if (<= j y)
      (if (<= i x)
        (recur (inc i) j (assoc-in matrix [j i] (setter i j matrix)))
        (recur 1 (inc j) matrix))
      matrix)))

(defn common-child-length [first-string second-string]
  (let [x (count first-string)
        y (count second-string)
        matrix (create-matrix (inc x) (inc y))
        setter (get-value-setter first-string second-string)]

    (get-in (visit matrix x y setter) [y x])))



