(ns otus-06.homework
    (:require [clojure.spec.alpha :as s]
              [clojure.string :as str]))

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
             (into [])
             (map (partial s/conform spec))
             )))

(comment
    (read-db-file->map "resources/homework/cust.txt" ::customer)
    (read-db-file->map "resources/homework/prod.txt" ::product)
    (read-db-file->map "resources/homework/sales.txt" ::sales))


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

(comment
    (customer-id->customer-name (load-customer) (nth (load-sales) 3))
    (update-record (load-customer) :customer-id :name :customer-name (nth (load-sales) 3))
    )

(defn sales->view []
    (let [sales-table (load-sales)
          customer-table (load-customer)
          product-table (load-product)
          sales-view (map (comp
                              (product-id->product-description product-table)
                              (customer-id->customer-name customer-table)) sales-table)]
        sales-view))

(comment
    (sales->view)
    )

(def actions {:display-customer-table load-customer
      :display-product-table  load-product
      :display-sales-table sales->view})