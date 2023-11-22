(ns otus-04.homework.scramblies)

;; Оригинальная задача:
;; https://www.codewars.com/kata/55c04b4cc56a697bb0000048

(defn scramble?
  "Вариант 1"
  [letters word]
  (let [f1 (frequencies letters)
        f2 (frequencies word)
        f2-size (count f2) true-count
        true-count (count (for [[k v] f2 :let [y (<= v (get f1 k 0))] :while (true? y)] y))]
    (= f2-size true-count)))

(defn scramble-v2?
  "Вариант 2"
  [letters word]
  (let [f1 (frequencies letters)
        f2  (frequencies word)
        ms (into {} (for [[k v] f2] [k (* -1 v)]))
        res (empty? (filter #(neg? (second %)) (merge-with + f1 ms)))]
    res))

(comment
  (scramble? "katas" "steak")

  (scramble-v2? "katas" "steak"))
