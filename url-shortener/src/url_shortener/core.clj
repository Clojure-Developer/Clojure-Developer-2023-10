(ns url-shortener.core
  (:require
   [clojure.string :as string]))


(def symbols
  "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")


;; =============================================================================
;; Number -> String
;; =============================================================================


(defn get-idx [i]
  (Math/floor (/ i 62)))


(defn get-symbol-by-idx [i]
  (get symbols (rem i 62)))


(defn id->url [id]
  (let [idx-sequence  (iterate get-idx id)
        valid-idxs    (take-while #(> % 0) idx-sequence)
        code-sequence (map get-symbol-by-idx valid-idxs)]
    (string/join (reverse code-sequence))))




(comment
 (get-idx 1000)
 (Math/floor (/ 1000 62))

 (take 10 (iterate get-idx 10))
 (take 10 (iterate get-idx 100))
 (take 10 (iterate get-idx 5000))

 (get-symbol-by-idx 5000)

 (id->url 12345) ;; "dnh"
 (id->url 3294233727)) ;;"dK6qQd"


;; =============================================================================
;; String -> Number
;; =============================================================================


(defn url->id [url]
  (let [url-symbols (seq url)]
    (reduce
     (fn [id symbol]
       (+ (* id 62)
          (string/index-of symbols symbol)))
     0
     url-symbols)))


(comment
 (url->id "dnh") ;; 12345
 (url->id "dK6qQd")) ;; 3294233727
