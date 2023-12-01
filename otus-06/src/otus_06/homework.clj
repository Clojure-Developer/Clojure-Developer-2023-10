(ns otus-06.homework
  (:require [clojure.java.io :as io])
  (:gen-class))

;; Загрузить данные из трех файлов на диске.
;; Эти данные сформируют вашу базу данных о продажах.
;; Каждая таблица будет иметь «схему», которая указывает поля внутри.
;; Итак, ваша БД будет выглядеть так:

;; cust.txt: это данные для таблицы клиентов. Схема:
;; <custID, name, address, phoneNumber>

;; Примером файла cust.txt может быть:
;; 1|John Smith|123 Here Street|456-4567
;; 2|Sue Jones|43 Rose Court Street|345-7867
;; 3|Fan Yuhong|165 Happy Lane|345-4533

;; Каждое поле разделяется символом «|». и содержит непустую строку.

;; prod.txt: это данные для таблицы продуктов. Схема
;; <prodID, itemDescription, unitCost>

;; Примером файла prod.txt может быть:
;; 1|shoes|14.96
;; 2|milk|1.98
;; 3|jam|2.99
;; 4|gum|1.25
;; 5|eggs|2.98
;; 6|jacket|42.99

;; sales.txt: это данные для основной таблицы продаж. Схема:
;; <salesID, custID, prodID, itemCount>.
;;
;; Примером дискового файла sales.txt может быть:
;; 1|1|1|3
;; 2|2|2|3
;; 3|2|1|1
;; 4|3|3|4

;; Например, первая запись (salesID 1) указывает, что Джон Смит (покупатель 1) купил 3 пары обуви (товар 1).

;; Задача:
;; Предоставить следующее меню, позволяющее пользователю выполнять действия с данными:

;; *** Sales Menu ***
;; ------------------
;; 1. Display Customer Table
;; 2. Display Product Table
;; 3. Display Sales Table
;; 4. Total Sales for Customer
;; 5. Total Count for Product
;; 6. Exit

;; Enter an option?


;; Варианты будут работать следующим образом

;; 1. Вы увидите содержимое таблицы Customer. Вывод должен быть похож (не обязательно идентичен) на

;; 1: ["John Smith" "123 Here Street" "456-4567"]
;; 2: ["Sue Jones" "43 Rose Court Street" "345-7867"]
;; 3: ["Fan Yuhong" "165 Happy Lane" "345-4533"]

;; 2. То же самое для таблицы prod.

;; 3. Таблица продаж немного отличается.
;;    Значения идентификатора не очень полезны для целей просмотра,
;;    поэтому custID следует заменить именем клиента, а prodID — описанием продукта, как показано ниже:
;; 1: ["John Smith" "shoes" "3"]
;; 2: ["Sue Jones" "milk" "3"]
;; 3: ["Sue Jones" "shoes" "1"]
;; 4: ["Fan Yuhong" "jam" "4"]

;; 4. Для варианта 4 вы запросите у пользователя имя клиента.
;;    Затем вы определите общую стоимость покупок для этого клиента.
;;    Итак, для Сью Джонс вы бы отобразили такой результат:
;; Sue Jones: $20.90

;;    Это соответствует 1 паре обуви и 3 пакетам молока.
;;    Если клиент недействителен, вы можете либо указать это в сообщении, либо вернуть $0,00 за результат.

;; 5. Здесь мы делаем то же самое, за исключением того, что мы вычисляем количество продаж для данного продукта.
;;    Итак, для обуви у нас может быть:
;; Shoes: 4

;;    Это представляет три пары для Джона Смита и одну для Сью Джонс.
;;    Опять же, если продукт не найден, вы можете либо сгенерировать сообщение, либо просто вернуть 0.

;; 6. Наконец, если выбрана опция «Выход», программа завершится с сообщением «До свидания».
;;    В противном случае меню будет отображаться снова.


;; *** Дополнительно можно реализовать возможность добавлять новые записи в исходные файлы
;;     Например добавление нового пользователя, добавление новых товаров и новых данных о продажах


;; Файлы находятся в папке otus-06/resources/homework

;; -------------- Работа с файлами и локальной бд ----------------
(def db
  "Локальная БД"
  (atom {}))

(defn parse-line
  "Чтение строки из файла в мапу"
  [ids line]
  (->> (clojure.string/split line #"\|")
       (mapv #(cond
                (re-matches #"\d+" %) (parse-long %)
                (re-matches #"\d+\.\d+" %) (parse-double %)
                :else %))
       (zipmap ids)))

(defn extract-id
  "Извлечение id как ключа для всей записи"
  [[id & rest] m]
  [(m id) (dissoc m id)])

(defn parse-file
  "Читаем файл из ресурсов, и парсим в табличку бд"
  [ids fn]
  (->> fn
       io/resource
       io/reader
       line-seq
       (mapv #(extract-id ids (parse-line ids %)))
       (into {})))

(defn init-db
  "Загрузка файлов в БД"
  []
  (println "Init local DB")
  (reset! db {:customers (parse-file [:custID :name :address :phoneNumber] "homework/cust.txt")
              :products (parse-file [:prodID :itemDescription :unitCost] "homework/prod.txt")
              :sales (parse-file [:salesID :custID :prodID :itemCount] "homework/sales.txt")}))

;;  ------------- Реализация пунктов меню -----------------

;;  -------------- 1 и 2 пункты меню ----------------

(defn print-db-tbl
  "Печать таблицы бд"
  [tbl-name]
  (doseq [[k v] (@db tbl-name)
          :let [arr (mapv #(str "\"" % "\"") (vals v))]]
    (println k " " arr)))

;; --------------- 3 пункт меню ---------------

(defn print-db-sales
  "Печать таблицы продаж в необходимом виде"
  []
  (doseq [[k sales-v] (@db :sales)
          :let [cust-id (sales-v :custID)
                cust-name (get-in @db [:customers cust-id :name])
                prod-id (sales-v :prodID)
                item (get-in @db [:products prod-id :itemDescription])
                cnt (sales-v :itemCount)
                arr [cust-name item cnt]]]
    (println k " " (mapv #(str "\"" % "\"") arr))))

;; -------- общее для 4 и 5 пункта меню ----------------

(defn row-id-by-column-value
  "Поиск id строки по содержимому колонки"
  [table column value]
  (first
   (for [[id {col-value column}] (@db table)
         :when (= col-value value)]
     id)))

;;  ------------- 4 пункт меню -----------------

(defn total-sales-by-cust
  "Получено денег от клиента, для теста"
  [name]
  (let [cust-id (row-id-by-column-value :customers :name name)]
    (->> (for [[_id {cid :custID pid :prodID cnt :itemCount}] (@db :sales)
               :when (= cid cust-id)
               :let [cost (get-in (@db :products) [pid :unitCost])]]
           (* cnt cost))
         (reduce +))))


(defn print-total-sales-by-cust
  "Получено денег от клиента, вариант для меню"
  []
  (println "Customer name:")
  (let [name (read-line)
        total-sales (total-sales-by-cust name)]
    (println name (format ": $%.2f" (float total-sales)))))

;; ------------- 5 пункт меню -----------------

(defn total-count-for-product
  "Количество проданых товаров, для теста"
  [name]
  (let [prod-id (row-id-by-column-value :products :itemDescription name)]
    (->> (for [[_id {pid :prodID cnt :itemCount}] (@db :sales)
               :when (= pid prod-id)]
           cnt)
         (reduce +))))


(defn print-total-count-for-product
  "Количество проданых товаров, для меню"
  []
  (println "Product name:")
  (let [name (read-line)
        cnt (total-count-for-product name)]
    (println (clojure.string/capitalize name) ": " cnt)))

;; ------------ Меню ------------------

(def menu-str
  "Представление меню"
  (str
   "*** Sales Menu ***\n"
   "1. Display Customer Table\n"
   "2. Display Product Table\n"
   "3. Display Sales Table\n"
   "4. Total Sales for Customer\n"
   "5. Total Count for Product\n"
   "6. Exit\n"))

(defn menu []
  (println "Press 'Enter' for display menu.")
  (read-line)
  (println menu-str)
  (print "Select menu item: ")
  (flush)
  (let [selected (read-line)
        _ (println)]
    (cond
      (= selected "1") #(do (print-db-tbl :customers) (menu))
      (= selected "2") #(do (print-db-tbl :products) (menu))
      (= selected "3") #(do (print-db-sales) (menu))
      (= selected "4") #(do (print-total-sales-by-cust) (menu))
      (= selected "5") #(do (print-total-count-for-product) (menu))
      (= selected "6") (println "Bye.")
      :else #(menu))))

(defn -main [& args]
  (init-db)
  (trampoline menu))

(comment
  (main))