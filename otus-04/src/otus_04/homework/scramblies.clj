(ns otus-04.homework.scramblies)

;; Оригинальная задача:
;; https://www.codewars.com/kata/55c04b4cc56a697bb0000048

(defn scramble?
  "Функция возвращает true, если из букв в строке letters
  можно составить слово word."
  [letters word]
  (let [letters-set (set (mapv str letters))
        word-set (set (mapv str word))]
    (= (clojure.set/intersection letters-set word-set) word-set)))
