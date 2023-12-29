(ns otus-06.homework
  (:require [clojure.string :as string])
  (:require [clojure.java.io :as io])
  (:require [clojure.set :as set]))

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

;; VARS
;;**************************************************

(def files ["cust.txt" "prod.txt" "sales.txt"])

;; запросы
(def menu "\n*** Sales Menu ***
------------------
1. Display Customer Table
2. Display Product Table
3. Display Sales Table
4. Total Sales for Customer
5. Total Count for Product
6. Exit
           
Enter an option\n")

(def cust-req "Enter a customer name\n")
(def item-req "Enter an item\n")

;; ключи для чтения и отображения
(def cust-keys [:custID :name :address :phoneNumber])
(def prod-keys [:prodID :itemDescription :unitCost])
(def sales-keys [:salesID :custID :prodID :itemCount])
(def sales-keys-to-show [:salesID :name :itemDescription :itemCount])

(def key-names [cust-keys prod-keys sales-keys])

;; ключи для конвертации и расчета total-sum
(def keys-to-int [:itemCount :prodID :custID :salesID])
(def keys-to-double [:unitCost])
(def keys-to-count-total-sum [:unitCost :itemCount])

;; ключи для агрегации
(def keys-to-cust-aggr [:name :totalCost])
(def keys-to-item-aggr [:itemDescription :itemCount])

;; FUNCTIONS
;;**********************************************************************

(defn read-to-maps 
  "читаем файл с соответствующими keys в список мап" 
  [f k] 
  (let [r (io/reader (io/resource f))] 
    (->> (line-seq r) 
         (map #(string/split % #"\|")) 
         (map #(zipmap k %)))))

(defn update-mult-vals 
  "преобразуем несколько столбов (keys), например, конвертируем" 
  [map vals fun] 
  (reduce #(update-in % [%2] fun) map vals))

(defn add-new-key 
  "добавляем новый столбец (key), рассчитанный из нескольких существующих" 
  [map keys name fun] 
  (assoc map name (reduce fun (vals (select-keys map keys)))))

(defn select-data 
  "выделяем несколько столбцов и преобразуем каждую строку к виду мапа (ID - данные)" 
  [keys-to-select key-to-first coll] 
  (->> coll 
       (map #(select-keys % keys-to-select)) 
       (map #((fn [x key] (merge {(key x) (dissoc x key)})) % key-to-first))))

(defn aggregate 
  "агрегируем данные aggr-key, группируя по name-key" 
  [[name-key aggr-key] coll] 
  (->> coll 
       (map #(select-keys % [name-key aggr-key])) 
       (group-by name-key) 
       (map (fn [[name vals]] 
              {name-key name 
               aggr-key (reduce + (map aggr-key vals))})) 
       (map #((fn [x key] {(key x) (dissoc x key)}) % name-key)) 
       (reduce merge)))

(defn aggregate-and-filter 
  "агрегируем и фильтруем результат по запрошенному значению name-key" 
  [[name-key aggr-key] message coll] 
  (println message) 
  (flush) 
  (let [filter-input (read-line)] 
    (->> coll 
         (aggregate [name-key aggr-key]) 
         (#(find % filter-input)))))

(defn print-result 
  "печатаем либо результат, либо уточняющий запрос" 
  [x] 
  (if-not (nil? x) 
    (run! println x) 
    (println "Precise your input\n")))


;; собираем все данные в одну таблицу (список мап)
(def full-data (->> (map #(read-to-maps %1 %2) files key-names)
                     (reduce set/join)
                     (map (fn [x] (update-mult-vals x keys-to-int #(Integer/parseInt %))))
                     (map (fn [x] (update-mult-vals x keys-to-double parse-double)))
                     (map (fn [x] (add-new-key x keys-to-count-total-sum :totalCost *)))))

(defn main 
  "результирующая функция"
  [coll] 
  (println menu) 
  (flush) 
  (let [x (read-line)] 
    (println (str x "\n")) 
    (->> (cond 
           (= x "1") (select-data cust-keys :custID coll) 
           (= x "2") (select-data prod-keys :prodID coll) 
           (= x "3") (select-data sales-keys-to-show :salesID coll) 
           (= x "4") (aggregate-and-filter keys-to-cust-aggr cust-req coll) 
           (= x "5") (aggregate-and-filter keys-to-item-aggr item-req coll) 
           (= x "6") ["Good Bye\n"]) 
         print-result) 
    (if (not= x "6") (main coll) nil)))
 
(main full-data)
