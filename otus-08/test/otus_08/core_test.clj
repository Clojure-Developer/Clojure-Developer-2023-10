(ns otus-08.core-test
  (:require [clojure.test :refer :all]
            [otus-08.core :refer :all]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]))

(deftest a-test)

(deftest test-matching-with-explicit-matchers
  (is (match? (m/equals 37) (+ 29 8)))
  #_(is (= 37 (+ 29 8)))
  (is (match? (m/regex #"fox") "The quick brown fox jumps over the lazy dog")))

(deftest test-matching-scalars
  ;; most scalar values are interpreted as an `equals` matcher
  (is (match? 37 (+ 29 8)))
  (is (match? "this string" (str "this" " " "string")))
  (is (match? :this/keyword (keyword "this" "keyword")))
  ;; regular expressions are handled specially
  (is (match? #"fox" "The quick brown fox jumps over the lazy dog")))

(deftest test-matching-sequences
  ;; A sequence is interpreted as an `equals` matcher, which specifies
  ;; count and order of matching elements. The elements, themselves,
  ;; are matched based on their types.
  (is (match? [1 3] [1 3]))
  (is (match? [1 odd?] [1 3]))
  (is (match? [#"red" #"violet"] ["Roses are red" "Violets are ... violet"]))

  ;; use m/prefix when you only care about the first n items
  (is (match? (m/prefix [odd? 3]) [1 3 5]))

  ;; use m/in-any-order when order doesn't matter
  (is (match? (m/in-any-order [odd? odd? even?]) [1 2 3]))

  ;; NOTE: in-any-order is O(n!) because it compares every expected element
  ;; with every actual element in order to find a best-match for each one,
  ;; removing matched elements from both sequences as it goes.
  ;; Avoid applying this to long sequences.
  )

(deftest test-matching-sets
  ;; A set is also interpreted as an `equals` matcher.
  (is (match? #{1 2 3} #{3 2 1}))
  (is (match? #{odd? even?} #{1 2}))
  ;; use m/set-equals to repeat predicates
  (is (match? (m/set-equals [odd? odd? even?]) #{1 2 3}))

  ;; NOTE: matching sets is an O(n!) operation because it compares every
  ;; expected element with every actual element in order to find a best-match
  ;; for each one, removing matched elements from both sets as it goes.
  ;; Avoid applying this to large sets.
  )

(deftest test-matching-maps
  ;; A map is interpreted as an `embeds` matcher, which ignores
  ;; un-specified keys
  (is (match? {:name/first "Alfredo"}
              {:name/first  "Alfredo"
               :name/last   "da Rocha Viana"
               :name/suffix "Jr."})))

(deftest test-matching-nested-datastructures
  ;; Maps, sequences, and sets follow the same semantics whether at
  ;; the top level or nested within a structure.
  (is (match? {:band/members [{:name/first "Alfredo"}
                              {:name/first "Benedito"}]}
              {:band/members [{:name/first  "Alfredo"
                               :name/last   "da Rocha Viana"
                               :name/suffix "Jr."}
                              {:name/first "Benedito"
                               :name/last  "Lacerda"}]
               :band/recordings []}))
  #_"Модификация матчеров"
  #_(is (match? (m/equals {:band/members [{:name/first "Alfredo"}
                                          {:name/first "Benedito"}]})
                {:band/members [{:name/first  "Alfredo"
                                 :name/last   "da Rocha Viana"
                                 :name/suffix "Jr."}
                                {:name/first "Benedito"
                                 :name/last  "Lacerda"}]
                 :band/recordings []})))

(deftest test-matching-transformed-value-via-via
  ;; via applies read-string to the actual value "{:foo :bar}" before
  ;; matching against the expected value {:foo :bar}
  (is (match? {:payloads [(m/via read-string {:foo :bar})]}
              {:payloads [" {:foo :bar} "]})))

(deftest exception-matching
  (is (thrown-match? clojure.lang.ExceptionInfo
                     {:foo 1}
                     (throw (ex-info "Boom!" {:foo 1 :bar 2})))))
