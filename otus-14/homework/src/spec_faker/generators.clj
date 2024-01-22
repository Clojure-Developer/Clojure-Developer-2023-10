(ns spec-faker.generators
    (:require [clojure.spec.alpha :as s]))

(def generators {"spec-int64"  (s/gen int?)
                 "spec-string" (s/gen int?)
                 "spec-bool"   (s/gen boolean?)})
