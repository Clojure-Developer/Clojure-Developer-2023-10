(ns otus-02.core
  (:require (otus-02.conditionals))
  (:import (java.io File BufferedReader)))



;; otus-02.core    имя нейспейса
;; otus_02/core    путь на файловой системе


;; встроенные неймспейсы
;;clojure.core
;;Math
;;System

;; текущий неймспейс
*ns*


;; подключаемся в другой неймспейс
(in-ns 'otus-02.conditionals)

;; не существующий нейспейс
(in-ns 'ns-wo-file)

;; дефолтный неймспейс
(in-ns 'user)

(in-ns 'otus-02.core)



;; подключаем другой неймспейс
(require '[otus-02.conditionals])

;; алиас
(require '[otus-02.conditionals :as cnd :reload])

;; импортируем только то что нужно
(require '[otus-02.conditionals :refer [ten eleven]])
(require '[otus-02.conditionals :refer [eleven] :rename {eleven twelve}])

(use '[otus-02.conditionals])


(ns-publics 'otus-02.conditionals)
(ns-resolve 'otus-02.conditionals 'eleven)

(find-ns 'my-new-namespace)
(create-ns 'my-new-namespace)


(System/getProperty "java.class.path")
