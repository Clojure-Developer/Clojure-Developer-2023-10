(ns otus-25.core
  (:require [clojure.walk :refer [macroexpand-all]])
  (:gen-class))

;; * Макросы

;; ** quote и eval

(quote (asd qwe hjk [123 :foo (asd)]))

(eval (concat (quote (+ 1) (list 2 3))))

;; ** defmacro

;; (append-2-3 (+ 1)) => (+ 1 2 3)
;; (append-2-3 (println)) => (println 2 3)
(defmacro append-2-3 [expr]
  (concat expr (list 2 3)))

;; ** Отладка

(macroexpand-1 '(append-2-3 (println)))
(macroexpand '(append-2-3 (println)))

(macroexpand-1 '(append-2-3 (when true)))
(macroexpand '(append-2-3 (when true)))

(macroexpand-all '(cond (< 1 2) 42
                        nil :foo
                        true :oops))

;; ** syntax-quote

(let [x 42] `(+ ~(+ 3 4) ~x ~x))

(defmacro append-2-3 [expr]
  `(~@expr 2 3))

(defmacro when? [cond & body]
  `(if ~cond
     (do ~@(apply concat (map (fn [_] body)) [1 2 3]))))

(macroexpand-1 (when? true
                 (println 1)
                 (println 2)))

;; ** анафорические макросы

(defmacro inject-x [& body]
  `(let [~'x 42] ~@body))

(comment
  (inject-x (println x)))

;; ** gensym и #

(defmacro tap [expr]
  (let [name (gensym)]
    `(let [~name ~expr]
       (println '~expr "=>" ~name)
       ~name)))

(comment
  (tap (+ 1 2)))

(defmacro tap [expr]
  `(let [name# ~expr]
     (println '~expr "=>" name#)
     name#))

(macroexpand-1
 '(tap (+ 1 3)))

;; ** &env и &form

(comment
  (@#some-macro '(some-macro x) {'x nil} 'x))
