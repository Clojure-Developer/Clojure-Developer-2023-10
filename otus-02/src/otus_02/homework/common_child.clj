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


(defn common-child-length [first-string second-string]
    (let [n (count first-string)
          m (count second-string)]
        (loop [i 1
               j 1
               lcs (vec (repeat (inc n) (vec (repeat (inc m) 0))))]
            (if (and (= i (inc n)) (= j 1))
                (last (last lcs))
                (let [c1 (nth first-string (dec i))
                      c2 (nth second-string (dec j))
                      val (if (= c1 c2)
                              (+ (get-in lcs [(dec i) (dec j)]) 1)
                              (max (get-in lcs [i (dec j)]) (get-in lcs [(dec i) j])))
                      new-lcs (assoc lcs i (assoc (get lcs i) j val))]
                    (if (= j m)
                        (recur (inc i) 1 new-lcs)
                        (recur i (inc j) new-lcs)))))))

(comment
    (let [matrix [[1 2 3] [4 5 6] [7 8 6]]]
        (get-in matrix [1 2])
        (assoc (get matrix 1) 0 1))
    (common-child-length "HARRY" "SALLY")
    (nth "HARRY" 0))