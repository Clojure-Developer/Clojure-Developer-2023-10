(ns otus-04.core)

;; * Описание коллекций
;; ** Списки

(list 1, 2, 3)

(cons 1 (cons 2 (cons 3 nil)))

'(1 (sdf 2 + 2) 3)

(quote (1 2 3))

;; ** Вектора

[1 2 3]

(vector 1 2 3)

(vec '(1 2 3))

["Bob" 42]

;; ** Отображения

{"a" 1
 "b" true
 [:a "asd"] 42}

(hash-map :a 1 :b 4)

;; ** Множества

#{1 2 3}
(set [1 2 3])

;; * Получение доступа к элементам
;; ** get,nth,first,last,rest

(first '(1 2 3))
(first {:a 1 :b 2})

(last '(1 2 3))
(rest '(1 2 3))
(rest [1 2 3])

(nth (list 1 2 3) 2)
(nth [1 2 3 4 5] 3)

(get ["a" "b" "c"] 1)
(get (get {:a 1 :b 2} :c) :d)

;; ** Коллекции как функции

(#{:red :black} :green)

({:a 1 :b 2} :c 42)

(:c {:a 1 :b 2} 42)

;; * Модификация коллекций
;; ** Добавление: conj, assoc

(conj '(1 2 3) 0)
(conj [1 2 3] 0)
(conj #{1 2} 2)
(conj {:a 1 :b 2} [:c 3] [:d 5])

(assoc [1 2 3] 1 100)
(assoc [1 2 3] 3 100)
(assoc {:a 1}
       :a 42
       :b 100)

;; ** Удаление ключей: dissoc

(dissoc {:a 1 :b 2} :b :c)

;; ** Обновление: assoc, update

(assoc {:a 1 :b 1} :b 3)

(update {:a 1 :b 2} :b + 50 50) ;; (+ 2 50 50)

;; * Работа с последовательностями
;; ** Отображение (mapping) значений

(map inc [1 2 3])
(mapv inc [1 2 3])

(map (fn [pair] [(second pair) (first pair)])
     {:a 1 :b 2})

;; does (seq {:a 1 :b 2}) under the hood

(map :shape
     [{:x 100 :y 200 :shape :circle}
      {:x 120 :y 50 :shape :triangle}])

;; ** Фильтрация

(filter odd? [1 2 3 4 5 6])
(filter #{:a :b :c} '(:a :b :x :y :a))

;; ** Агрегация

(reduce + [1 2 3]) ;; (+ (+) (+ 1 (+ 2 (+ 3))))

;; ** List comprehensions

(for [x [1 2 3]
      y [10 20 31 40]
      :let [z (+ x y)]
      :when (odd? z)]
  z)

;; * Вложенные структуры

(def data
  {:users
   [{:name "Bob"
     :age 24
     :pets [{:kind :cat
             :name "Thomas"}
            {:kind :mouse
             :name "Jerry"}]}
    {:name "Alice"
     :age 12
     :pets [{:kind :cat
             :name "Cheshire"}]}
    {:name "Shagie"
     :age 18
     :pets [{:kind :dog
             :name "Scooby Doo"}]}
    {:name "Nimnul"
     :age 50
     :pets [{:kind :cat
             :name "Fatcat"}]}]})

(get (get (get (get (get data :users) 0) :pets) 1) :name)

(get-in data [:users 0 :pets 1 :name] :oops)
(get-in data [:users 0 :pets 5 :name] :oops)

(update-in data [:users 0 :age] inc)
;; data["users"][0]["age"] += 1

(update-in data [:users 0 :pets] first)

(update-in data [:users 0 :age] inc)

;; * Деструктуризация

(let [line [[10 10] [10 100]]
      [[x1 _] [x2 _]] line]
  (= x1 x2))

(let [[_ x & [_ & xs]] {:a 1 :b 2 :c 3 :d 10 :e 100}]
  [x xs])

(let [line {:begin {:x 100 :y 101}
            :end {:x 200 :y 100}}

      {{x :x y :y} :begin} line]
  [x y])

(let [line {:begin {:x 100 :y 100}
            :end {:x 200 :y 100}}

      {{:keys [x y]} :begin} line]
  [x y])

(defn name-of-first-user [data]
  (let [{[{n :name}] :users} data]
    n))

(name-of-first-user data)

(defn name-of-first-user [{[{n :name}] :users}]
  n)

(defn my+ [x & xs]
  (apply + x xs))

(my+ 1 2 3 4 5)

(defn vertical? [[[x1 _] [x2 _]]]
  (= x1 x2))

(let [line [[10 10] [10 100]]]
  (vertical? line))

;; ** комплексный пример
(set
 (for [{ps1 :pets n1 :name} (:users data)
       {ps2 :pets n2 :name} (:users data)
       :when (not= n1 n2)
       {k1 :kind} ps1 :when (= k1 :cat)
       {k2 :kind} ps2 :when (= k2 :cat)]
   (set [n1 n2])))
