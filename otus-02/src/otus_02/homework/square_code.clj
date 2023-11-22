(ns otus-02.otus-02.homework.square-code
(:require [clojure.math :as math] [clojure.string :as string]))

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
  (let [st    (->> input string/lower-case
                   (filter #(Character/isLetter %)))
        sqr   (math/sqrt (count st))
        rnd   (math/round sqr)
        [x y] (if (= (int (math/ceil sqr)) rnd) [(inc rnd) rnd] [rnd rnd])]
    (->> (repeat (inc x) (range (inc y)))
         flatten
         (map vector (concat st (repeat \space)))
         (sort-by second)
         (map first) 
         drop-last 
         (apply str))))

(defn decode-string [input]
  (->> (range)
       (map #(vector % %))
       flatten 
       (#(map vector % (drop 1 %)))
       (drop-while #(< (reduce * %) (count input)))
       first 
       (#(repeat (first %) (range (second %))))
       flatten
       (map vector (concat input (repeat \space)))
       (sort-by second ) 
       (map first) 
       (apply str) 
       (string/trim)))
