(ns otus-04.homework.scramblies)

;; Оригинальная задача:
;; https://www.codewars.com/kata/55c04b4cc56a697bb0000048

(defn nil-to-zero-wrapper [value]
    (if (nil? value)
        0
        value))

(defn compare-freq [coll1 coll2 letter]
    (>= (nil-to-zero-wrapper (get coll1 letter))
        (get coll2 letter)))

(defn scramble?
    "Функция возвращает true, если из букв в строке letters
    можно составить слово word."
    [letters word]
    (let [freq-letters (frequencies letters)
          freq-word (frequencies word)]
        (every? identity
                (map (partial compare-freq freq-letters freq-word) (keys freq-word)))))

(comment
    (frequencies "asdasde")
    (get {\a 2, \s 2, \d 2, \e 1} \a)
    (scramble? "rkqodlw" "word")
    (>= (get (frequencies "rkqodlw") \w) (get (frequencies "word") \w)))
