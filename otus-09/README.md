# Clojure Developer. Lesson 9

## Polimorphism in Clojure. Part 1

> In programming language theory and type theory, polymorphism is the provision of a single interface to entities of different types or the use of a single symbol to represent multiple different types. The concept is borrowed from a principle in biology where an organism or species can have many different forms or stages.

The most commonly recognized major forms of polymorphism are:

* Ad hoc polymorphism: defines a common interface for an arbitrary set of individually specified types.
* Parametric polymorphism: not specifying concrete types and instead use abstract symbols that can substitute for any type.
* Subtyping (also called subtype polymorphism or inclusion polymorphism): when a name denotes instances of many different classes related by some common superclass.

### case/cond

* [cond](https://clojuredocs.org/clojure.core/cond)
* [case](https://clojuredocs.org/clojure.core/case)
* [instance?](https://clojuredocs.org/clojure.core/instance_q)

### clojure.core.match

[clojure.core.match](https://github.com/clojure/core.match)

### multimethods

[Runtime Polymorphism](https://clojure.org/about/runtime_polymorphism)

[Multimethods and Hierarchies](https://clojure.org/reference/multimethods)

Multimetod:

* a *dispatching function* ([defmulti](https://clojuredocs.org/clojure.core/defmulti))
* one or more *methods* ([defmethod](https://clojuredocs.org/clojure.core/defmethod))

[methods](https://clojuredocs.org/clojure.core/remove-method)
[remove-methods](https://clojuredocs.org/clojure.core/remove-method)

[Understanding Clojure Multimethods](https://dev.to/kelvinmai/understanding-clojure-multimethods-2cd0)

[Functional Polymorphism using Clojure’s Multimethods](https://ilanuzan.medium.com/functional-polymorphism-using-clojures-multimethods-825c6f3666e6)

[Integrant](https://github.com/weavejester/integrant#usage)

#### ad-hoc иерархии

* [isa?](https://clojuredocs.org/clojure.core/isa_q)
* [derive](https://clojuredocs.org/clojure.core/derive)
* [prefer-method](https://clojuredocs.org/clojure.core/prefer-method)
* [make-hierarchy](https://clojuredocs.org/clojure.core/make-hierarchy)
* [parents](https://clojuredocs.org/clojure.core/parents)
* [ancestors](https://clojuredocs.org/clojure.core/ancestors)
* [descendants](https://clojuredocs.org/clojure.core/descendants)
