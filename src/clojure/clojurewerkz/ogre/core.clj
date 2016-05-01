(ns clojurewerkz.ogre.core
  (:refer-clojure :exclude [and count drop filter group-by key key identity iterate loop map max min next not or range repeat reverse shuffle])
  (:require [potemkin :as po]
            [clojurewerkz.ogre.util :as util]
            [clojurewerkz.ogre.anon :as anon])
  (:import (org.apache.tinkerpop.gremlin.process.traversal Traversal Compare P Order)
           (org.apache.tinkerpop.gremlin.structure T)
           (org.apache.tinkerpop.gremlin.process.traversal.dsl.graph __)
           (java.util Iterator)
           (org.apache.tinkerpop.gremlin.process.traversal.dsl.graph GraphTraversal GraphTraversalSource)))

(po/import-macro util/query)
(po/import-vars [clojurewerkz.ogre.anon
                __bothE __is __map])

; GraphTraversalSource
(defn add-V
  ([^GraphTraversalSource g]
    (.addV g))
  ([^GraphTraversalSource g label]
    (.addV g ^String (util/cast-param label))))

(defn E
  "Returns all edges matching the supplied ids.
  If no ids are supplied, returns all edges."
  [^GraphTraversalSource g & ids]
  (.E g (into-array ids)))

(defn inject
  [^GraphTraversalSource g & starts]
  (.inject g (into-array starts)))

(defn V
  "Returns all vertices matching the supplied ids.
  If no ids are supplied, returns all vertices."
  [^GraphTraversalSource g & ids]
  (.V g (into-array ids)))

; GraphTraversal

(defn add-E
  [^GraphTraversal t label]
  (.addE t ^String (util/cast-param label)))

(defn add-V
  ([^GraphTraversal t]
   (.addV t))
  ([^GraphTraversal t label]
   (.addV t ^String (util/cast-param label))))

(defn aggregate
  [^GraphTraversal t k]
  (.aggregate t k))

(defn and
  [^GraphTraversal t & traversals]
  (.and t (into-array traversals)))

(defn as
  [^GraphTraversal t step-label & step-labels]
  (.as t step-label (into-array step-labels)))

(defn barrier
  ([^GraphTraversal t]
   (.barrier t))
  ([^GraphTraversal t max-or-consumer]
   (if (instance? clojure.lang.IFn max-or-consumer)
     (.barrier t (util/f-to-consumer max-or-consumer))
     (.barrier t (int max-or-consumer)))))

(defn both
  [^GraphTraversal t & labels]
  (let [label-array (util/keywords-to-str-array labels)]
    (.both t label-array)))

(defn bothE
  [^GraphTraversal t & labels]
  (let [label-array (util/keywords-to-str-array labels)]
    (.bothE t label-array)))

(defn bothV
  [^GraphTraversal t]
  (.bothV t))

(defn branch
  [^GraphTraversal t f-or-t]
  (if (instance? Traversal f-or-t)
    (.branch t ^Traversal f-or-t)
    (.branch t (util/f-to-function f-or-t))))

(defn by
  ([^GraphTraversal t]
   (.by t))
  ([^GraphTraversal t arg1]
   (cond
     (keyword? arg1)
     (.by t ^String (util/cast-param arg1))
     (instance? String arg1)
     (.by t ^String arg1)
     (instance? java.util.Comparator arg1)
     (.by t ^java.util.Comparator arg1)
     (instance? clojure.lang.IFn arg1)
     (.by t (util/f-to-function arg1))
     (instance? Order arg1)
     (.by t ^Order arg1)
     (instance? T arg1)
     (.by t ^T arg1)
     (instance? Traversal arg1)
     (.by t ^Traversal arg1)))
  ([^GraphTraversal t arg1 ^java.util.Comparator compar]
   (cond
     (instance? clojure.lang.IFn arg1)
     (.by t (util/f-to-function arg1 compar))
     (instance? String arg1)
     (.by t ^String arg1 compar)
     (instance? Traversal arg1)
     (.by t ^Traversal arg1 compar))))

(defn cap
  [^GraphTraversal t k & ks]
  (.cap t k (into-array ks)))

; TODO: choose()

(defn coin
  [^GraphTraversal t prob]
  (.coin t prob))

(defn constant
  [^GraphTraversal t c]
  (.constant t c))

(defn count
  ([^GraphTraversal t]
   (.count t))
  ([^GraphTraversal t scope]
   (.count t scope)))

(defn cyclic-path
  [^GraphTraversal t]
  (.cyclicPath t))

(defn dedup
  ([^GraphTraversal t labels]
   (.dedup t (into-array labels)))
  ([^GraphTraversal t scope labels]
   (.dedup t scope (into-array labels))))

(defn drop
  [^GraphTraversal t]
  (.drop t))

(defn emit
  ([^GraphTraversal t]
   (.emit t))
  ([^GraphTraversal t pred-or-t]
   (if (instance? Traversal pred-or-t)
     (.emit t ^Traversal pred-or-t)
     (.emit t (util/f-to-predicate pred-or-t)))))

(defn filter
  [^GraphTraversal t pred-or-t]
  (if (instance? Traversal pred-or-t)
    (.filter t ^Traversal pred-or-t)
    (.filter t (util/f-to-predicate pred-or-t))))

(defn flat-map
  [^GraphTraversal t f-or-t]
  (if (instance? Traversal f-or-t)
    (.flatMap t ^Traversal f-or-t)
    (.flatMap t (util/f-to-function f-or-t))))

(defn fold
  ([^GraphTraversal t]
   (.fold t))
  ([^GraphTraversal t seed fold-function]
   (.fold t seed (util/f-to-bifunction fold-function))))

(defn from
  ([^GraphTraversal t t-or-label]
   (if (instance? Traversal t-or-label)
     (.from t ^Traversal t-or-label)
     (.from t ^String t-or-label))))

(defn group
  ([^GraphTraversal t]
   (.group t))
  ([^GraphTraversal t k]
   (.group t k)))

(defn group-count
  ([^GraphTraversal t]
   (.groupCount t))
  ([^GraphTraversal t k]
   (.groupCount t k)))

(defn has
  "Allows an element if it has the given property or it satisfies given predicate."
  ([^GraphTraversal t k]
   (.has t (util/cast-param k)))
  ([^GraphTraversal t k val-or-pred-or-t]
   (let [arg1 (util/cast-param k)]
     (cond
       (instance? String arg1)
       (cond
         (instance? P val-or-pred-or-t)
         (.has t ^String arg1 ^P val-or-pred-or-t)
         (instance? Traversal val-or-pred-or-t)
         (.has t ^String arg1 ^Traversal val-or-pred-or-t)
         :else (.has t ^String arg1 ^Object val-or-pred-or-t))
       (instance? T arg1)
       (cond
         (instance? P val-or-pred-or-t)
         (.has t ^T arg1 ^P val-or-pred-or-t)
         (instance? Traversal val-or-pred-or-t)
         (.has t ^T arg1 ^Traversal val-or-pred-or-t)
         :else (.has t ^T arg1 ^Object val-or-pred-or-t)))))
  ([^GraphTraversal t label k val-or-pred]
   (let [arg2 (util/cast-param k)
         arg1 (util/cast-param label)]
     (if (instance? P val-or-pred)
       (.has t ^String arg1 ^String arg2 ^P val-or-pred)
       (.has t ^String arg1 ^String arg2 ^Object val-or-pred)))))

(defn has-id
  [^GraphTraversal t & ids]
  (let [id-array (into-array Object ids)]
    (.hasId t id-array)))

(defn has-key
  [^GraphTraversal t & ks]
  (let [k-array (util/keywords-to-str-array ks)]
    (.hasKey t k-array)))

(defn has-label
  [^GraphTraversal t & labels]
  (let [label-array (util/keywords-to-str-array labels)]
    (.hasLabel t label-array)))

(defn id
  [^GraphTraversal t]
  (.id t))

(defn identity
  [^GraphTraversal t]
  (.identity t))

(defn in
  [^GraphTraversal t & labels]
  (let [label-array (util/keywords-to-str-array labels)]
    (.in t label-array)))

(defn inE
  [^GraphTraversal t & labels]
  (let [label-array (util/keywords-to-str-array labels)]
    (.inE t label-array)))

(defn inject
  [^GraphTraversal t & args]
  (.inject t (into-array args)))

(defn inV
  [^GraphTraversal t]
  (.inV t))

(defn is
  [^GraphTraversal t val-or-pred]
  (if (instance? P val-or-pred)
    (.is t ^P val-or-pred)
    (.is t ^Object val-or-pred)))

(defn key
  [^GraphTraversal t]
  (.key t))

(defn label
  [^GraphTraversal t]
  (.label t))

(defn limit
  ([^GraphTraversal t lim]
   (.limit t lim))
  ([^GraphTraversal t scope lim]
   (.limit t scope lim)))

(defn local
  [^GraphTraversal t local-traversal]
  (.local t local-traversal))

(defn loops
  [^GraphTraversal t]
  (.loops t))

(defn map
  [^GraphTraversal t f-or-t]
  (if (instance? Traversal f-or-t)
    (.map t ^Traversal f-or-t)
    (.map t (util/f-to-function f-or-t))))

(defn match
  [^GraphTraversal t & traversals]
  (.match t (into-array traversals)))

(defn max
  ([^GraphTraversal t]
   (.max t))
  ([^GraphTraversal t scope]
   (.max t scope)))

(defn mean
  ([^GraphTraversal t]
   (.mean t))
  ([^GraphTraversal t scope]
   (.mean t scope)))

(defn min
  ([^GraphTraversal t]
   (.min t))
  ([^GraphTraversal t scope]
   (.min t scope)))

(defn not
  ([^GraphTraversal t not-traversal]
   (.not t not-traversal)))

(defn option
  ([^GraphTraversal t opt-traversal]
   (.option t opt-traversal))
  ([^GraphTraversal t pick-token opt-traversal]
   (.option t pick-token opt-traversal)))

(defn optional
  ([^GraphTraversal t opt-traversal]
   (.optional t opt-traversal)))

(defn or
  [^GraphTraversal t & traversals]
  (.or t (into-array traversals)))

(defn order
  ([^GraphTraversal t]
   (.order t))
  ([^GraphTraversal t scope]
   (.order t scope)))

(defn otherV
  [^GraphTraversal t]
  (.otherV t))

(defn out
  [^GraphTraversal t & labels]
  (let [label-array (util/keywords-to-str-array labels)]
    (.out t label-array)))

(defn outE
  [^GraphTraversal t & labels]
  (let [label-array (util/keywords-to-str-array labels)]
    (.outE t label-array)))

(defn outV
  [^GraphTraversal t]
  (.outV t))

(defn page-rank
  ([^GraphTraversal t]
   (.pageRank t))
  ([^GraphTraversal t alpha]
   (.pageRank t alpha)))

(defn path
  [^GraphTraversal t]
  (.path t))

(defn peer-pressure
  [^GraphTraversal t]
  (.peerPressure t))

(defn profile
  ([^GraphTraversal t k]
   (.profile t k)))

;; excluded program()

(defn project
  [^GraphTraversal t k & ks]
  (.project t k (into-array ks)))

(defn properties
  [^GraphTraversal t & ks]
  (.properties t (util/keywords-to-str-array ks)))

;; TODO: property()

(defn property-map
  [^GraphTraversal t & ks]
  (.propertyMap t (util/keywords-to-str-array ks)))

(defn range
  ([^GraphTraversal t low high]
   (.range t low high))
  ([^GraphTraversal t scope low high]
   (.range t scope low high)))

(defn repeat
  [^GraphTraversal t repeat-traversal]
  (.repeat t repeat-traversal))

(defn sack
  ([^GraphTraversal t]
   (.sack t))
  ([^GraphTraversal t sack-op]
   (.sack t sack-op))
  ([^GraphTraversal t sack-op ks]
   (.sack t sack-op (util/keywords-to-str-array ks))))

(defn sample
  ([^GraphTraversal t amount]
   (.sample t amount))
  ([^GraphTraversal t scope amount]
   (.sample t scope amount)))

;; TODO: select

(defn side-effect
  [^GraphTraversal t c-or-t]
  (if (instance? Traversal c-or-t)
    (.sideEffect t ^Traversal c-or-t)
    (.sideEffect t (util/f-to-consumer c-or-t))))

(defn simple-path
  [^GraphTraversal t]
  (.simplePath t))

(defn store
  [^GraphTraversal t k]
  (.store t k))

(defn subgraph
  [^GraphTraversal t k]
  (.subgraph t k))

(defn sum
  ([^GraphTraversal t]
   (.sum t))
  ([^GraphTraversal t scope]
   (.sum t scope)))

(defn tail
  ([^GraphTraversal t]
   (.tail t))
  ([^GraphTraversal t arg1]
   (if (instance? ^Scope arg1)
     (.sack t ^Scope arg1)
     (.sack t (long arg1))))
  ([^GraphTraversal t scope lim]
   (.tail t scope lim)))

(defn time-limit
  [^GraphTraversal t lim]
  (.timeLimit t lim))

(defn times
  [^GraphTraversal t loops]
  (.times t loops))

;; TODO: to()

(defn tree
  ([^GraphTraversal t]
   (.tree t))
  ([^GraphTraversal t k]
   (.tree t k)))

(defn unfold
  [^GraphTraversal t]
  (.unfold t))

(defn union
  [^GraphTraversal t & traversals]
  (.union t (into-array traversals)))

(defn until
  [^GraphTraversal t pred-or-t]
  (if (instance? Traversal pred-or-t)
    (.until t ^Traversal pred-or-t)
    (.until t (util/f-to-predicate pred-or-t))))

(defn midV
  [^GraphTraversal t & ids]
  (.V t (into-array ids)))

(defn value
  [^GraphTraversal t]
  (.value t))

;; TODO: valueMap()

(defn values
  [^GraphTraversal t & ks]
  (let [k-array (util/keywords-to-str-array ks)]
    (.values t k-array)))

(defn where
  ([^GraphTraversal t p-or-t]
   (if (instance? Traversal p-or-t)
     (.where t ^Traversal p-or-t)
     (.where t ^P p-or-t)))
  ([^GraphTraversal t k p]
   (.where t k p)))

