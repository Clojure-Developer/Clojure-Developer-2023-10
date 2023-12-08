(ns otus-06.homework
    (:require [clojure.string :as str]))

(defn parse-int [val]
    (Long/parseLong val))

(defn parse-double [val]
    (Double/parseDouble val))

(def customer-header {:customer-id  parse-int
                      :name         identity
                      :address      identity
                      :phone-number identity})

(def product-header {:product-id       parse-int
                     :item-description identity
                     :unit-cost        parse-double})

(def sales-header {:sales-id    parse-int
                   :customer-id parse-int
                   :product-id  parse-int
                   :item-count  parse-int})

(defn assoc-parsed-value [header result [k v]]
    (assoc result k ((get header k) v)))

(defn read-db-file->map [file-name header]
    (with-open [rdr (clojure.java.io/reader file-name)]
        (->> rdr
             line-seq
             (into [])
             (map #(str/split % #"\|"))
             (map (partial zipmap (keys header)))
             (map (partial reduce (partial assoc-parsed-value header) {}))
             )))

(comment
    (read-db-file->map "resources/homework/cust.txt" customer-header)
    (read-db-file->map "resources/homework/prod.txt" product-header)
    (read-db-file->map "resources/homework/sales.txt" sales-header)
    (assoc-parsed-value customer-header {} [:customer-id "1"])
    (let [k :customer-id]
        (get customer-header k)))