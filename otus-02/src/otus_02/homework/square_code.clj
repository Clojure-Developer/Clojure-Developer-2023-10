(ns otus-02.homework.square-code
  (:require [clojure.math :as math]
            [clojure.string :as str]))

(defn- pad-right [len val col]
  (take len (concat col (repeat val))))

(defn- get-col-width [input-string]
  (-> input-string
      (count)
      (math/sqrt)
      (math/ceil)
      (int)))

(defn- prepare-str [input]
  (-> input
      (str/replace #"[\W]{1,}" "")
      (str/lower-case)))

(defn- str->matrix [input]
  (let [len (get-col-width input)]
    (->> input
         (partition-all len)
         (map (fn [lst] (pad-right len " " lst))))))

(defn encode-string [input]
  (->> input
       (prepare-str)
       (str->matrix)
       (apply mapv str)
       (str/join " ")))

(defn decode-string [ciphertext]
  (let [normalized (str/replace ciphertext #"([\w]{1,})([\W]{1,1})" "$1")
        length (count normalized)
        side (Math/sqrt length)
        rows (Math/floor side)
        cols (Math/ceil side)]
    (str/join
     (apply concat (for [i (range rows)]
                     (for [j (range cols)]
                       (let [value
                             (get normalized
                                  (int (+ (* j rows) i))
                                  "")]
                         (when-not (= value \space)
                           value))))))))

