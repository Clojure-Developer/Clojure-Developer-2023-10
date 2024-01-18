(ns otus-06.homework
    (:require [clojure.spec.alpha :as s]
              [clojure.string :as str])
    (:gen-class))

(s/def ::str->int
    (s/conformer (fn [val]
                     (try (Long/parseLong val)
                          (catch Exception _
                              ::s/invalid)))
                 str))

(s/def ::str->double
    (s/conformer (fn [val]
                     (try (Double/parseDouble val)
                          (catch Exception _
                              ::s/invalid)))
                 str))

(s/def ::table-line->vals
    (s/conformer (fn [val]
                     (try (str/split val #"\|")
                          (catch Exception _
                              ::s/invalid)))
                 (fn [val]
                     (str/join "|" val))))

(s/def ::customer
    (s/and ::table-line->vals
           (s/cat :customer-id ::str->int
                  :name any?
                  :address any?
                  :phone-number any?)))

(s/def ::product
    (s/and ::table-line->vals
           (s/cat :product-id ::str->int
                  :item-description any?
                  :unit-cost ::str->double)))

(s/def ::sales
    (s/and ::table-line->vals
           (s/cat :sales-id ::str->int
                  :customer-id ::str->int
                  :product-id ::str->int
                  :item-count ::str->int)))

(defn read-db-file->map [file-name spec]
    (with-open [rdr (clojure.java.io/reader file-name)]
        (->> rdr
             line-seq
             (map (partial s/conform spec))
             doall
             )))

(defn load-customer []
    (read-db-file->map "resources/homework/cust.txt" ::customer))

(defn load-product []
    (read-db-file->map "resources/homework/prod.txt" ::product))

(defn load-sales []
    (read-db-file->map "resources/homework/sales.txt" ::sales))


(defn update-record [src-table
                     fk-kw
                     src-kw
                     trg-kw                                 ;TODO rework to concat old + table name from meta
                     record]
    (-> record
        (assoc trg-kw ((comp src-kw first)
                       (filter #(= (fk-kw %) (fk-kw record)) src-table)))
        (dissoc fk-kw)))

(defn customer-id->customer-name [customer-table]
    (partial update-record customer-table :customer-id :name :customer-name))


(defn product-id->product-description [product-table]
    (partial update-record product-table :product-id :item-description :product-description))

(defn product-id->product-unit-cost [product-table]
    (partial update-record product-table :product-id :unit-cost :product-unit-cost))

(defn get-sales-view []
    (let [sales-table (load-sales)
          customer-table (load-customer)
          product-table (load-product)
          sales-view (map (comp
                              (product-id->product-description product-table)
                              (customer-id->customer-name customer-table)) sales-table)]
        sales-view))

(defn calculate-income-by-client-name []
    (flush)
    (let [customer-name (read-line)
          sales-table (load-sales)
          customer-table (load-customer)
          product-table (load-product)
          view (map (comp
                        (product-id->product-unit-cost product-table)
                        (customer-id->customer-name customer-table)) sales-table)]
        (->> view
             (filter #(= customer-name (:customer-name %)))
             (reduce #(+ %1 (* (:item-count %2) (:product-unit-cost %2))) 0))))

(defn calculate-sales-count-by-product-name []
    (flush)
    (let [product-name (read-line)
          sales-table (load-sales)
          product-table (load-product)
          view (map (product-id->product-description product-table) sales-table)]
        (->> view
             (filter #(= product-name (:product-description %)))
             (reduce #(+ %1 (:item-count %2)) 0))))

(def actions-hint "--------------------------------\n
    1. Display Customer Table\n
    2. Display Product Table\n
    3. Display Sales Table\n
    4. Total Sales for Customer\n
    5. Total Count for Product\n
    6. Display Hint\n
    7. Exit\n\nEnter an option?")

(defn print-hint []
    (println actions-hint))

(def actions [load-customer
              load-product
              get-sales-view
              calculate-income-by-client-name
              calculate-sales-count-by-product-name
              print-hint])


(defn choose-action []
    (let [action-id (parse-long (read-line))]
        (cond
            (and (>= action-id 1) (<= action-id (count actions)))
            (do (clojure.pprint/pprint ((get actions (dec action-id)))) choose-action)

            (= action-id (inc (count actions)))
            (println "Bye-bye!")

            :else
            (do (println "Incorrect action number. Try again.") choose-action))))

(defn start []
    (print-hint)
    (trampoline choose-action))
(comment
    (start))