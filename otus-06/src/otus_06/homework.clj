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