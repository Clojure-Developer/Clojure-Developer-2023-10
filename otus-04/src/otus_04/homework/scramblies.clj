(ns otus-04.homework.scramblies)

;; Оригинальная задача:
;; https://www.codewars.com/kata/55c04b4cc56a697bb0000048

(defn scramble? [letters word]
  "Функция возвращает true, если из букв в строке letters
  можно составить слово word."
  (let [f-freq  (fn [x] (map #(get (frequencies x) % 0) (set word)))
        [x y]   (map f-freq [letters word])]
          (every? true? (map >= x y))))
