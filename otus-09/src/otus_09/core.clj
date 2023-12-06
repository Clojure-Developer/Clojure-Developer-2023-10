(ns otus-09.core
  (:require [clojure.core.match :refer [match]]
            [clojure.math :as math])
  (:gen-class))

;; helpers

(defn ->rect [width height]
  {:shape :rect      ;; kinda type
   :width width
   :height height})

(defn ->circle [radius]
  {:shape :circle    ;; kinda type
   :radius radius})

(defn unknown-shape-ex [shape]
  (ex-info "Unknown shape" {:shape shape}))

(def rect (->rect 4 13))
(def circle (->circle 12))


;; case

(defn area [object]
  (case (:shape object)
    :rect   (* (:width object) (:height object))
    :circle (* math/PI (:radius object) (:radius object))
    (throw  (unknown-shape-ex object))))

(comment
  (area rect)
  
  (area circle)
  
  (area {})
  )


;; cond + instance?
;; https://aphyr.com/posts/352-clojure-from-the-ground-up-polymorphism

(defn append
  "Adds an element x to the end of any sequential collection,
   faster for vectors."
  [coll x]
  (cond
    (instance? clojure.lang.PersistentVector coll)
    (conj coll x)

    (instance? clojure.lang.IPersistentList coll)
    (concat coll (list x))

    :else (str "Sorry, I don't know how to append to a " (type coll))))

(comment
  (append [1 2 3] 5)
  
  (append '(1 2 3) 5)
  
  (append {} 1)
  )


;; clojure.core.match

(doseq [n (range 1 17)]
  (println
   (match [(mod n 3) (mod n 5)]
          [0 0] "FizzBuzz"
          [0 _] "Fizz"
          [_ 0] "Buzz"
          :else n)))



(defn area-match [object]
  (match object
    {:shape :rect :width w :height h}
    (* w h)

    {:shape :circle :radius r}
    (* math/PI r r)

    :else
    (throw (unknown-shape-ex object))))

(comment
  (area-match rect)
  
  (area-match circle)
  
  (area-match {})
  )


;; multimethods
;; defmulti — обычная функция с механизмом выбора и семантикой defonce

(defmulti area-multi :shape)

(defmethod area-multi :rect
  [object]
  (* (:width object) (:height object)))

(defmethod area-multi :circle
  [object]
  (* Math/PI (:radius object) (:radius object)))

(defmethod area-multi :default [object]
  (throw (unknown-shape-ex object)))

(comment
  (area-multi rect)
  
  (area-multi circle)
  
  (area-multi {})
  )


;; multimethods: recursion

(defmulti factorial identity)

(defmethod factorial 0
  [_]
  1)

(defmethod factorial :default
  [num]
  (* num (factorial (dec num))))

(comment
  (factorial 5))


(defmulti recursive identity)

(defmethod recursive 1 recursive-impl
  [cnt]
  (if (< cnt 5)
    (do (println cnt)
        (recursive-impl (inc cnt)))
    cnt))

(comment
  (recursive 1))


;; multimethods: calculating a dispatch value

(def quick-sort-threshold 5)

(defn dispatch-sort [array]
  (if (every? integer? array)
    :counting-sort
    (if (< (count array) quick-sort-threshold)
      :quick-sort
      :merge-sort)))

(defmulti my-sort dispatch-sort)

(defmethod my-sort :counting-sort
  [_]
  "Counting for the win!")

(defmethod my-sort :quick-sort
  [_]
  "Quick Sort it is")

(defmethod my-sort :merge-sort
  [_]
  "Good ol' Merge Sort")

(comment
  (my-sort [1 2 3])
  
  (my-sort [1 2 3 "a"])
  
  (my-sort [1 2 3 4 "a"]))


;; multimethods: more examples
;; https://clojure.org/about/runtime_polymorphism

(defmulti encounter (fn [x y] [(:species x) (:species y)]))

(defmethod encounter [:bunny :lion]
  [_ _]
  :run-away)

(defmethod encounter [:lion :bunny]
  [_ _]
  :eat)

(defmethod encounter [:lion :lion]
  [_ _]
  :fight)

(defmethod encounter [:bunny :bunny]
  [_ _]
  :mate)

(def b1 {:species :bunny :other :stuff})
(def b2 {:species :bunny :other :stuff})
(def l1 {:species :lion :other :stuff})
(def l2 {:species :lion :other :stuff})

(comment
  (encounter b1 b2)
  
  (encounter b1 l1) 

  (encounter l1 b1) 

  (encounter l1 l2)
  
  (map encounter
       [b1 b1 l1 l1]
       [b2 l1 b1 l2])
  )


;; ad-hoc иерархии

(defmulti describe class)

(defmethod describe ::collection [c]
  (format "%s is a collection" c))

(defmethod describe String [s]
  (format "%s is a string" s))

(comment
  (isa? :radius :radius)
  (isa? java.util.Map ::collection)
  (isa? java.util.Collection ::collection)


  (derive java.util.Map ::collection)
  (derive java.util.Collection ::collection)

  (describe [])

  (describe (java.util.HashMap.))

  (describe "bar")

  (parents java.util.AbstractMap)
  (ancestors java.util.AbstractMap)
  
  (descendants ::collection)
  )



;; prefer-method
;; https://clojuredocs.org/clojure.core/prefer-method

;; Типичная ситуация: делаем выбор по типу (java class), но в Java
;; разрешена имплементация нескольких интерфейсов. Как быть?)

(defmulti edges
  "Retrieves first and last from a collection"
  type)

(defmethod edges java.lang.Iterable [x]
  ((juxt first last) (seq x)))

(defmethod edges clojure.lang.IPersistentList [x] 
  ((juxt first last) (seq x)))

(comment
  (edges [1 2 3 4 5]) 
  
  (edges (list 1 2 3 4 5))
  ;; IllegalArgumentException Multiple methods in multimethod 'edges'
  
  (prefer-method edges clojure.lang.IPersistentList java.lang.Iterable)
  
  ;; now it works
  (edges (list 1 2 3 4 5))
  )
