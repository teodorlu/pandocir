(ns pandocir.hiccup
  (:require [clojure.walk :as walk]))

(defmulti ir->hiccup-1 :pandocir/type)

;; Inline
(defmethod ir->hiccup-1 :pandocir.type/str [ir-node]
  (:pandocir/text ir-node))

(defmethod ir->hiccup-1 :pandocir.type/emph [ir-node]
  (into [:em] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/underline [ir-node]
  (into [:u] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/strong [ir-node]
  (into [:strong] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/strikeout [ir-node]
  (into [:del] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/superscript [ir-node]
  (into [:sup] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/subscript [ir-node]
  (into [:sub] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/small-caps [ir-node]
  (into [:span {:class ["smallcaps"]}] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/quoted [ir-node]
  (let [[open close] (case (:pandocir/type (:pandocir.quote/type ir-node))
                       :pandocir.type/single-quote ["‘" "’"]
                       :pandocir.type/double-quote ["“" "”"])]
    (concat [open] (:pandocir/inlines ir-node) [close])))

(defmethod ir->hiccup-1 :pandocir.type/cite [ir-node]
  :pandocir.error/cite-not-implemented)

(defmethod ir->hiccup-1 :pandocir.type/code [ir-node]
  (let [{:pandocir.attr/keys [id classes keyvals]} (:pandocir/attr ir-node)
        ks (map (comp keyword (partial str "data-") first) keyvals)
        attributes (cond-> (zipmap ks (map second keyvals))
                     (seq id) (assoc :id id)
                     (seq classes) (assoc :class classes))]
    [:code attributes (:pandocir/text ir-node)]))1

(defmethod ir->hiccup-1 :pandocir.type/space [_ir-node]
  " ")

(defmethod ir->hiccup-1 :pandocir.type/soft-break [_ir-node]
  " ")

(defmethod ir->hiccup-1 :pandocir.type/line-break [ir-node]
  :pandocir.error/line-break-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/math [ir-node]
  :pandocir.error/math-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/raw-inline [ir-node]
  :pandocir.error/raw-inline-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/link [ir-node]
  :pandocir.error/link-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/image [ir-node]
  :pandocir.error/image-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/note [ir-node]
  :pandocir.error/note-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/span [ir-node]
  :pandocir.error/span-not-implemented)

;; Block
(defmethod ir->hiccup-1 :pandocir.type/plain [ir-node]
  :pandocir.error/plain-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/para [ir-node]
  :pandocir.error/para-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/line-block [ir-node]
  :pandocir.error/line-block-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/code-block [ir-node]
  :pandocir.error/code-block-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/raw-block [ir-node]
  :pandocir.error/raw-block-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/block-quote [ir-node]
  :pandocir.error/block-quote-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/ordered-list [ir-node]
  :pandocir.error/ordered-list-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/bullet-list [ir-node]
  :pandocir.error/bullet-list-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/definition-list [ir-node]
  :pandocir.error/definition-list-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/header [ir-node]
  :pandocir.error/header-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/horizontal-rule [ir-node]
  :pandocir.error/horizontal-rule-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/table [ir-node]
  :pandocir.error/table-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/figure [ir-node]
  :pandocir.error/figure-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/div [ir-node]
  :pandocir.error/div-not-implemented)

;; Arguments
(defmethod ir->hiccup-1 :pandocir/attr [ir-node]
  :pandocir.error/not-implemented)
(defmethod ir->hiccup-1 :pandocir/list-attr [ir-node]
  :pandocir.error/not-implemented)

(defmethod ir->hiccup-1 :default [x]
  x)

(defn ir->hiccup [ir]
  (walk/postwalk ir->hiccup-1 ir))
