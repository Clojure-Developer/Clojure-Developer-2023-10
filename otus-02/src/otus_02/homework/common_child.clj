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

;; алготм взят из интернета, адаптирован под кложу.
;; Try - для того чтобы избежать выход за границы массива, пытался взять (nth -1 string),
;; доп условие в cond не помогло.
;; memoize - для того чтобы повторно не вычислял одинаковые варианты

(def lcs
  (memoize (fn [s1 s2 x y]
             (try
               (cond
                 (and (zero? x) (zero? y)) 0
                 (= (nth s1 (dec x)) (nth s2 (dec y))) (inc (lcs s1 s2 (dec x) (dec y)))
                 :else (max (lcs s1 s2 x (dec y)) (lcs s1 s2 (dec x) y)))
               (catch Exception e 0)))))


(defn common-child-length [first-string second-string]
  (lcs first-string second-string (count first-string) (count second-string)))







(comment
    ;; 3.42 , memoize - 1.25
  (time (lcs " ABCD " " ABDC " 4 4))
    ;;
  )
  



