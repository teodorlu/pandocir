(ns pandocir.core
  (:require
   [clojure.walk :as walk]
   [pandocir.hiccup]
   [pandocir.ir]))

(defn raw->ir [rawpandoc]
  (pandocir.ir/pandoc->ir rawpandoc))

(defn ir->raw [ir]
  (pandocir.ir/ir->pandoc ir))

(defn ir->hiccup [ir]
  (if-let [blocks (:blocks ir)]
    (seq (pandocir.hiccup/ir->hiccup blocks))
    (pandocir.hiccup/ir->hiccup ir)))

(defn- apply-filter [filters node]
  (let [filter (filters (:pandocir/type node))]
    (cond-> node filter filter)))

(defn postwalk
  "Traverses the given pandocir AST in a depth-first, post-order traversal, and
  applies the given filters. The filters is assumed to be a map with pandocir
  types (e.g. :pandocir.type/div) as keys and functions that take a pandocir
  node as input."
  [ir filters]
  (walk/postwalk (partial apply-filter filters) ir))

(defn prewalk
  "Traverses the given pandocir AST in a depth-first, pre-order traversal, and
  applies the given filters. The filters is assumed to be a map with pandocir
  types (e.g. :pandocir.type/div) as keys and functions that take a pandocir
  node as input."
  [ir filters]
  (walk/prewalk (partial apply-filter filters) ir))
