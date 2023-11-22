(ns otus-04.homework.scramblies)

;; Оригинальная задача:
;; https://www.codewars.com/kata/55c04b4cc56a697bb0000048

(defn scramble?
  "Функция возвращает true, если из букв в строке letters
  можно составить слово word."
  [letters word]
    (let [f-freq  (fn [x] (->> (set word)
                               (map #(get (frequencies x) % 0))))
         [x y]    (->> [letters word]
                       (map f-freq))]
      (->> (map >= x y)
           (reduce #(and %1 %2) true))))
