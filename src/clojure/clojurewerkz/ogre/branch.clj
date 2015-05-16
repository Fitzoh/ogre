(ns clojurewerkz.ogre.branch
  (:import (Traversal)
           (org.apache.tinkerpop.gremlin.process.traversal Traversal))
  (:require [clojurewerkz.ogre.util :refer (typed-traversal f-to-function f-to-predicate)]))

(defmacro choose
  "Select which branch to take based on predicate or jump map."
  ([^Traversal t k m]
   `(typed-traversal .choose ~t (f-to-function ~k)
                     ~(if (map? m)
                        (into {} (for [[k v] m]
                                   [k `(->  ~v)]))
                        m)))
  ([^Traversal t pred true-choice false-choice]
   `(typed-traversal .choose ~t (f-to-predicate ~pred)
                     (->  ~true-choice)
                     (->  ~false-choice))))

;; jump
;; until
