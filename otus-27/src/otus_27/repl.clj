(ns otus-27.repl
  (:require [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [next.jdbc.sql :as jdbc.sql]))

(comment
  ;; https://clojuredocs.org/clojure.core/*print-namespace-maps*
  (set! *print-namespace-maps* false) 
  (set! *print-namespace-maps* true))

;; next.jdbc

;; db-spec -> DataSource -> Connection
(def db {:dbtype "h2" ; h2:mem
         :dbname "example"})

(def ds (jdbc/get-datasource db))

(with-open [conn (jdbc/get-connection ds)]
  (jdbc/execute! conn ...))

;; Primary API
(jdbc/execute! ds ["select * from information_schema.tables"])

(reduce (fn [res row]
          (conj res
                (select-keys row
                             [:tables/table_name
                              :tables/table_type])))
        []
        (jdbc/plan ; [connectable sql-params opts]
         ds
         ["select * from information_schema.tables"]))


(jdbc/execute-one! ds ["
create table address (
  id int auto_increment primary key,
  name varchar(32),
  email varchar(255)
)"])

(jdbc/execute-one! ds ["
insert into address(name, email)
  values('Clojure Developer', 'developer@clojure.org')"])

(jdbc/execute! ds ["select * from address"])

(jdbc/execute-one! ds ["insert into address(name, email)
  values('Java Developer', 'developer@java.com')
"] {:return-keys true})

(jdbc/execute-one! ds ["select * from address where id = ?" 2])
(jdbc/execute-one! ds ["select * from address where id = ?" 3])

;; Options & Result Set Builders
;; https://cljdoc.org/d/com.github.seancorfield/next.jdbc/1.3.909/doc/all-the-options#generating-rows-and-result-sets
(require '[next.jdbc.result-set :as rs])

(jdbc/execute-one! ds ["select * from address where id = ?" 2]
                   {:builder-fn rs/as-unqualified-lower-maps})

;; plan & Reducing Result Sets
(jdbc/execute-one! ds ["
create table invoice (
  id int auto_increment primary key,
  product varchar(32),
  unit_price decimal(10,2),
  unit_count int,
  customer_id int
)"])

(jdbc/execute-one! ds ["
insert into invoice (product, unit_price, unit_count, customer_id)
values ('apple', 0.99, 6, 100),
       ('banana', 1.25, 3, 100),
       ('cucumber', 2.49, 2, 100)
"])

(reduce
 (fn [cost row]
   (+ cost (* (:unit_price row)
              (:unit_count row))))
 0
 (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

(transduce
 (map #(* (:unit_price %) (:unit_count %)))
 +
 0
 (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

(transduce
 (comp (map (juxt :unit_price :unit_count))
       (map #(apply * %)))
 +
 0
 (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

(transduce
 (map :unit_count)
 +
 0
 (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

;; set of unique products
(into #{}
      (map :product)
      (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

;; use run! for side-effects
(run! #(println (:product %))
      (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))


;; selects specific keys (as simple keywords):
(into []
      (map #(select-keys % [:id :product :unit_price :unit_count :customer_id]))
      (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

;; selects specific keys (as qualified keywords):
(into []
      (map #(select-keys % [:invoice/id :invoice/product
                            :invoice/unit_price :invoice/unit_count
                            :invoice/customer_id]))
      (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

;; selects specific keys (as qualified keywords -- ignoring the table name):
(into []
      (map #(select-keys % [:foo/id :bar/product
                            :quux/unit_price :wibble/unit_count
                            :blah/customer_id]))
      (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

;; do not do this:
(into []
      (map #(into {} %))
      (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))

;; https://clojure.org/reference/datafy
;; do this if you just want realized rows with default qualified names:
(into []
      (map #(rs/datafiable-row % ds {}))
      (jdbc/plan ds ["select * from invoice where customer_id = ?" 100]))


;; Datasources, Connections & Transactions
(with-open [con (jdbc/get-connection ds)]
  (jdbc/execute! con ...)
  (jdbc/execute! con ...)
  (into [] (map :column) (jdbc/plan con ...)))


(jdbc/with-transaction [tx ds]
  (jdbc/execute! tx ...)
  (jdbc/execute! tx ...)
  (into [] (map :column) (jdbc/plan tx ...)))

;; Joins
(jdbc/execute-one! ds ["
create table owners (
  id int auto_increment primary key,
  name varchar(255) not null
)"])

(jdbc/execute-one! ds ["
create table pets (
  id int auto_increment primary key,
  name varchar(255) not null,
  owner int not null
    references owners(id)
    on delete cascade
)"])

(jdbc/execute! ds ["insert into owners (name) values (?), (?)"
                   "Bob"
                   "Alice"])

;; Friendly SQL Functions
;; https://cljdoc.org/d/com.github.seancorfield/next.jdbc/1.3.909/doc/getting-started/friendly-sql-functions
(require '[next.jdbc.sql :as jdbc.sql])

(jdbc.sql/insert! ds :owners {:name "Tom"})

(jdbc/execute! ds ["select id, name from owners"])

(jdbc/with-transaction [tx ds]
  (jdbc.sql/insert-multi!
   tx
   :pets
   [{:name "Skipper" :owner 1}
    {:name "Spot"    :owner 2}
    {:name "Stinky"  :owner 2}
    {:name "Jerry"   :owner 3}]))

(jdbc.sql/find-by-keys ds :pets {:owner 2})

(jdbc/execute! ds ["
select
  o.name as owner,
  p.name as pet
from owners as o
  left join pets as p
    on p.owner = o.id
"])

;; HoneySQL
(require '[honey.sql :as sql])

(jdbc/execute!
 ds
 (sql/format {:from   [[:pets :p]]
              :select [:p.name :o.name]
              :where  [[:= :p.name [:param :?]]]
              :left-join [[:owners :o]
                          [:= :p.owner :o.id]]}
             {:params {:? "Skipper"}}))

(jdbc/execute!
 ds
 (sql/format '{from ((pets p))
               select (p.name, o.name)
               where ((= p.name (param :?)))
               left-join ((owners o)
                          (= p.owner o.id))}
             {:params {:? "Jerry"}}))

(require '[honey.sql.helpers :as h])

(jdbc/execute!
 ds
 (sql/format (-> {}
              (h/select :p.name :p.name)
              (h/from [:pets :p])
              (h/where [:= :p.name [:param :?]])
              (h/left-join [:owners :o]
                           [:= :p.owner :o.id]))
             {:params {:? "Jerry"}}))
