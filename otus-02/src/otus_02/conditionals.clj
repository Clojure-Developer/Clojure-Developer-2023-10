(ns otus-02.conditionals)


(def ten 10)
(def eleven 10)


;; identity and equality
;; = identical?

(= 1 1)

(= 1 2)

(= {:a 1 :b 2}
   {:a 1 :b 2})

(= {:a 1 :b 3}
   {:a 1 :b 2})

(= [1 2 {:a 1}]
   [1 2 {:a 1}])

(= [1 2 {:a 1}]
   '(1 2 {:a 1}))

(= [1 2 {:a 1}]
   '(1 2 {:a 2}))


(identical? {:a 1 :b 2}
            {:a 1 :b 2})



;; = not= > < >= <=
(= 1 2 3)

;; !=
(not= 1 2 3)

(> 3 1)

(<  3 2 1)
(>  3 2 1)

(<= 1 1)



;; only two falsey values
false nil


;; and or
(and 1 2 3 '() 4)

(and nil 2 3)
(and 1 false 3)

(and "" 2 3)

(or nil false false)

(= nil nil)

(or 1 2 3)

(or nil 3 4)

(or (= 1 1)
    (zero? (- 2 2)))



;; if if-not when when-not
(def n 1)

(if (= n 1)
  "one"
  "not one")

(not true)
(not false)

(if (not (> n 1))
  (print "less than one")
  (print "more than one"))

(if-not (> n 1)
  (let []
    (+ 1 2)
    (+ 1 2)
    (+ 1 2))
  "more than one")

(if (not (> n 1))
  (do
    (+ 1 2)
    (+ 1 2)
    (+ 1 2))
  "more than one")


(if (> n 1)
  "less than one")

(when (> n 1)
  (println "less than one")
  (+ 2 2)
  ())



;; case
(= n 1)

(case n
  (1 4 5 0) "one"
  2 "two"
  3 "three"
  :qwe "three"
  "unknown")

(def n 5)


#_(case 5
    1 "one"
    2 "two"
    3 "three")



;; cond condp
(def n 0)

(cond
  (< n 0) "negative"
  (> n 0) "positive"
  :else "zero")


(condp identical? n
  1 "one"
  2 "two"
  3 "three"
  (str "unexpected value, \"" n \"))



(some #{4 5 9} [1 2 3 4])

;; (pred test-expr expr)
(condp some [1 2 3 4]
  #{0 6 7} :>> inc
  #{4 5 9} :>> dec)
