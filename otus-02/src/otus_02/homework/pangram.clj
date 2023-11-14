(ns otus-02.homework.pangram
  (:require [clojure.string :as string]))

(def az-sum  (apply + (range (int \a) (inc (int \z))))) ;; 2847

(defn is-pangram [test-string]
  (= az-sum
     (->> test-string
          string/lower-case
          (map int)
          (filter #(<= 97  %  122)) ;;<= \a % \z
          set ;; убираем дубликаты
          (apply +))))

(comment
  (is-pangram "Sphinx of black quartz, judge my vow"))

