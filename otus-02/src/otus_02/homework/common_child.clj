(ns otus-02.homework.common-child
  (:require [clojure.string :as s]
            [clojure.set :refer [intersection]]))


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

(defn combinations
  "Ищем все возможные комбинации потомков в сторке с учетом порядка следования букв"
  [letters]
  (loop [result      nil
         rest-result letters]
    (if (empty? rest-result)
      #{result}  
      (let [current              (first rest-result)
            next-result          (conj result current)
            combinations-without (combinations (rest rest-result))
            combinations-with    (mapv #(str current %) combinations-without)]
        (set (concat combinations-with (combinations (rest rest-result))))))))

(defn delete-not-repeat-letter
  "Удаляем не повторяющиеся буквы в двух строках"
  [first-string second-string]
  (filterv identity
    (map (fn [x]
           (if (s/includes? second-string x)
             x)) first-string)))

(defn common-child-length
  "Вычисляем максимального потомка"
  [first-string second-string]
  (let [s1            (re-seq #"[\w+]" first-string) 
        s2            (re-seq #"[\w+]" second-string)
        s1-not-repeat (delete-not-repeat-letter s1 s2)
        s2-not-repeat (delete-not-repeat-letter s2 s1)]
    (reduce max
      (map count
        (some-> (intersection (combinations s1-not-repeat) (combinations s2-not-repeat)))))))

;;(time (common-child-length  "HARRY" "SALLY"))
;;=> "Elapsed time: 0.132041 msecs"
