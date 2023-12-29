## Clojure Developer. Lesson 10

Polimorphism in Clojure. Part 2

> All of Clojure’s dispatch mechanisms – interfaces, protocols, and multimethods – are implemented using tables.  All of them are open, but to different degrees. In general, their efficiency is inversely proportional to their openness.

*— Elements of Clojure*

* [Datatypes: deftype, defrecord and reify](https://clojure.org/reference/datatypes)
* [Protocols](https://clojure.org/reference/protocols)
* [Clojure from the ground up: polymorphism](https://aphyr.com/posts/352-clojure-from-the-ground-up-polymorphism)

> When performance matters, we turn to interfaces and protocols

* [definterface](https://clojuredocs.org/clojure.core/definterface)
* [reify](https://clojuredocs.org/clojure.core/reify)
* [proxy](https://clojuredocs.org/clojure.core/reify)

> The lesson learned from proxy is that unless you are forced to extend a class from a Java framework in order to use it, you should probably look into reify instead of proxy for the creation of quick throw-away instances. If instead your goal is polymorphism in Clojure, there are better options with protocols and multimethods.

### Protocols

There are several motivations for protocols:

* Provide a high-performance, dynamic polymorphism construct as an alternative to interfaces
* Support the best parts of interfaces
  * specification only, no implementation
  * a single type can implement multiple protocols
* While avoiding some of the drawbacks
  * Which interfaces are implemented is a design-time choice of the type author, cannot be extended later (although interface injection might eventually address this)
  * implementing an interface creates an isa/instanceof type relationship and hierarchy
* Avoid the 'expression problem' by allowing independent extension of the set of types, protocols, and implementations of protocols on types, by different parties
  * do so without wrappers/adapters
* Support the 90% case of multimethods (single dispatch on type) while providing higher-level abstraction/organization

[Expression Problem](https://wiki.c2.com/?ExpressionProblem)

* [defprotocol](https://clojuredocs.org/clojure.core/defprotocol)
* [extend-protocol](https://clojuredocs.org/clojure.core/extend-protocol)
* [deftype](https://clojuredocs.org/clojure.core/deftype)
* [defrecord](https://clojuredocs.org/clojure.core/defrecord)

### deftype vs defrecord

* `deftype` provides no functionality not specified by the user, other than a constructor
* `defrecord` provides a complete implementation of a persistent map, including:
  * value-based equality and hashCode
  * metadata support
  * associative support
  * keyword accessors for fields
  * extensible fields (you can assoc keys not supplied with the defrecord definition)
  * etc
* `deftype` supports mutable fields, defrecord does not
* `defrecord` supports an additional reader form of `#my.record{:a 1, :b 2}`
* when a `defrecord` `Bar` is defined a corresponding function `map->Bar` is defined that takes a map and initializes a new record instance with its contents
