(ns otus-02.homework.palindrome
    (:require [clojure.string :as string])
    (:import [java.lang Character]))

(defn convert-string-to-mass [st & [case-fun & _]]
    (->> st
         (filter (fn [x] (Character/isLetter ^char x)))
         (string/join)
         ((if (nil? case-fun)
              string/upper-case
              case-fun))))

(defn is-palindrome [test-string]                           ;; Why is there no question mark in func name?
    (let [letters-mass (convert-string-to-mass test-string)
          reversed-letter-mass (string/reverse letters-mass)]
        (= letters-mass reversed-letter-mass)))

(comment
    (convert-string-to-mass "фытак 92е2 п2 пак--п")
    (convert-string-to-mass "фытак 92е2 п2 пак--п" string/lower-case)
    ((resolve 'string/upper-case))
    (is-palindrome "as-ds?a")
    (is-palindrome "as-ds?aa")

    (def case-fun string/lower-case)

    (let [case-fun 'string/lower-case]
        (resolve
            (if (nil? case-fun)
                (string/upper-case)
                (case-fun))) "asd"

        (if (nil? case-fun)
            (partial string/upper-case)
            (partial case-fun)))
    ((resolve (symbol "+")) 2))

