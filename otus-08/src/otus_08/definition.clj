(ns otus-08.definition)

(defn- func
  []
  nil)

(defn ^:private func
  []
  nil)

(def ^:private variable
  nil)

(meta #'func)
(meta #'variable)

(set! *print-meta* true)

(defn get-file
  ^String [^Long x]
  x)

(defn ^String get-file
  [^Long x]
  x)

(defn get-file
  "Some docstring"
  {:error [:exception/null-pointer]
   :pre ()
   :post ()}
  ^String [^Long x]
  x)

(meta #'get-file)
