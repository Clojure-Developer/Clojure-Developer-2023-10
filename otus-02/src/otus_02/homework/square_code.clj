(ns otus-02.homework.square-code
  (:require [clojure.string :as string]))

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


(defn clean-str [s]
  (-> s
      clojure.string/lower-case
      (clojure.string/replace #"[^a-z]" "")))

(defn transpose
  "транспонирование матрицы"
   [m]
  (apply mapv vector m))

(defn encode-string [input]
  (let [s (clean-str input)
        col-size (int (Math/ceil (Math/sqrt (count s))))
        row-size (Math/ceil (/ (count s) col-size))
        end-spaces (repeat (- (* col-size row-size) (count s)) " ")]
    (->> s
         (partition col-size col-size end-spaces)
         transpose
         (mapv #(string/join "" %))
         (string/join " "))))


(defn decode-string [input]
  (let [s input
        col-size (int (Math/floor (Math/sqrt (count s))))]
    (->> s
         (partition col-size (inc col-size))
         transpose
         (mapv #(string/join "" %))
         (string/join "")
         string/trim)))


(comment

  (encode-string "If man was meant to stay on the ground, god would have given us roots.")

  (decode-string  "imtgdvs fearwer mayoogo anouuio ntnnlvt wttddes aohghn  sseoau "))