(ns otus-02.homework.square-code
    (:require [clojure.math :as math]
              [clojure.string :as str])
    (:use [otus-02.homework.palindrome :only [convert-string-to-mass]]))

;; Реализовать классический метод составления секретных сообщений, называемый `square code`.
;; Выведите закодированную версию полученного текста.

;; Во-первых, текст нормализуется: из текста удаляются пробелы и знаки препинания,
;; также текст переводится в нижний регистр.
;; Затем нормализованные символы разбиваются на строки.
;; Эти строки можно рассматривать как образующие прямоугольник при печати их друг под другом.

;; Например,
"If man was meant to stay on the ground, god would have given us roots."
;; нормализуется в строку:
"ifmanwasmeanttostayonthegroundgodwouldhavegivenusroots"

;; Разбиваем текст в виде прямоугольника.
;; Размер прямоугольника (rows, cols) должен определяться длиной сообщения,
;; так что c >= r и c - r <= 1, где c — количество столбцов, а r — количество строк.
;; Наш нормализованный текст имеет длину 54 символа
;; и представляет собой прямоугольник с c = 8 и r = 7:
"ifmanwas"
"meanttos"
"tayonthe"
"groundgo"
"dwouldha"
"vegivenu"
"sroots  "

;; Закодированное сообщение получается путем чтения столбцов слева направо.
;; Сообщение выше закодировано как:
"imtgdvsfearwermayoogoanouuiontnnlvtwttddesaohghnsseoau"

;; Полученный закодированный текст разбиваем кусками, которые заполняют идеальные прямоугольники (r X c),
;; с кусочками c длины r, разделенными пробелами.
;; Для фраз, которые на n символов меньше идеального прямоугольника,
;; дополните каждый из последних n фрагментов одним пробелом в конце.
"imtgdvs fearwer mayoogo anouuio ntnnlvt wttddes aohghn  sseoau "

;; Обратите внимание, что если бы мы сложили их,
;; мы могли бы визуально декодировать зашифрованный текст обратно в исходное сообщение:

"imtgdvs"
"fearwer"
"mayoogo"
"anouuio"
"ntnnlvt"
"wttddes"
"aohghn "
"sseoau "


(defn encode-string [input]
    (let [mass (convert-string-to-mass input clojure.string/lower-case)
          cnt (count mass)
          rows (int (math/floor (math/sqrt cnt)))
          cols (int (math/ceil (math/sqrt cnt)))
          mass (str mass (apply str (repeat (- (* rows cols) cnt) " ")))
          ans-without-spaces (loop [r-ind 0
                                    c-ind 0
                                    acc ""]
                                 (if (and (= r-ind 0)
                                          (= c-ind cols))
                                     acc
                                     (if (= r-ind (dec rows))
                                         (recur 0 (inc c-ind) (str acc (nth mass (+ c-ind (* r-ind cols)))))
                                         (recur (inc r-ind) c-ind (str acc (nth mass (+ c-ind (* r-ind cols)))))
                                         )))]
        (str/join " " (map #(reduce str %) (partition rows ans-without-spaces)))))


(comment
    (let [a "If man was meant to stay on the ground, god would have given us roots."]
        (encode-string a))
    (encode-string "Asdasda")
    (map #(reduce str %) (partition 4 "asdasdsadfasfa"))
    (map #(drop-last (reduce str %)) (partition 5 "asda sdsa dfas ")))


(defn decode-string [input]
    (let [mass (convert-string-to-mass input clojure.string/lower-case)
          cnt (count mass)
          rows (int (math/ceil (math/sqrt cnt)))
          cols (int (math/floor (math/sqrt cnt)))
          mass (str/join (map #(reduce str (drop-last %)) (partition rows (str input " " ))))
          ans-without-spaces (loop [r-ind 0
                                    c-ind 0
                                    acc ""]
                                 (if (and (= r-ind 0)
                                          (= c-ind cols))
                                     acc
                                     (if (= r-ind (dec rows))
                                         (recur 0 (inc c-ind) (str acc (nth mass (+ c-ind (* r-ind cols)))))
                                         (recur (inc r-ind) c-ind (str acc (nth mass (+ c-ind (* r-ind cols)))))
                                         )))]
        (str/replace ans-without-spaces " " "")))

(comment
    (decode-string "imtgdvs fearwer mayoogo anouuio ntnnlvt wttddes aohghn  sseoau "))