(ns clojurewerkz.ogre.filter
  (:refer-clojure :exclude [filter and or range])
  (:import (org.apache.tinkerpop.gremlin.structure Compare)
           (java.util Collection)
           (org.apache.tinkerpop.gremlin.process.traversal.dsl.graph GraphTraversal))
  (:require [clojurewerkz.ogre.util :refer (f-to-function f-to-predicate typed-traversal f-to-bipredicate f-to-compare)]))

(defn cyclic-path
  "The step analyzes the path of the traverser thus far and if there are any repeats, the traverser
  is filtered out over the traversal computation."
  [^GraphTraversal t]
    (typed-traversal .cyclicPath t))

(defn dedup
  "Filters out repeated objects. A function can be supplied that provides the
  values that the traversal will consider when filtering."
  ([^GraphTraversal t]
    (.dedup t)))

(defn except
  "Filters out the given objects."
  [^GraphTraversal t excepter]
    (cond
      (instance? String excepter) (typed-traversal .except t ^String excepter)
      (instance? Collection excepter) (typed-traversal .except t ^Collection excepter)
      :else (except t [excepter])))

(defn filter
  "Filters using a predicate that determines whether an object should pass."
  [^GraphTraversal t f] (typed-traversal .filter t (f-to-predicate f)))

(defn has
  "Allows an element if it has the given property or it satisfies given predicate."
  ([^GraphTraversal t k]
    (typed-traversal .has t (name k))))

(defn has-not
  "Allows an element if it does not have the given property."
  ([^GraphTraversal t k]
    (typed-traversal .hasNot t (name k))))

(defn interval
  "Allows elements to pass that have their property in the given start and end interval."
  [^GraphTraversal t key ^Comparable start ^Comparable end]
  (typed-traversal .between t (name key) start end))

(defn limit
  "Limit the number of elements to pass through Traversal."
  [^GraphTraversal t l] (typed-traversal .limit t l))

(defn coin
  "Allows elements to pass with the given probability."
  [^GraphTraversal t probability] (typed-traversal .coin t probability))

(defn range
  "Allows elements to pass that are within the given range."
  [^GraphTraversal t low high] (typed-traversal .range t low high))

(defn retain
  "Only allows the given objects to pass."
  [^GraphTraversal t retainer]
    (cond
      (instance? String retainer) (typed-traversal .retain t ^String retainer)
      (instance? Collection retainer) (typed-traversal .retain t ^Collection retainer)
      :else (retain t [retainer])))

(defn simple-path
  "Allows an element if the current path has no repeated elements."
  [^GraphTraversal t] (typed-traversal .simplePath t))

(defmacro where
  "Further constrain results from match with a binary predicate or traversal."
  ([^GraphTraversal t pred a b]
   `(typed-traversal .where ~t (name ~a) (name ~b) (f-to-bipredicate ~pred)))
  ([^GraphTraversal t constraint]
   `(typed-traversal .where ~t (-> ~constraint))))
