(ns pandocir.hiccup
  (:require [clojure.walk :as walk]
            [hiccup2.core :as h]))

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

(defmethod ir->hiccup-1 :pandocir.type/cite [_ir-node]
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

(defmethod ir->hiccup-1 :pandocir.type/line-break [_ir-node]
  [:br])

(defmethod ir->hiccup-1 :pandocir.type/math [_ir-node]
  :pandocir.error/math-not-implemented)

(defmethod ir->hiccup-1 :pandocir.type/raw-inline [ir-node]
  ;; Raw strings are treated differently in hiccup, reagent and replicant.
  ;; This solution is dependent on hiccup, which might not be ideal.
  (when (= (:pandocir/format ir-node) "html")
    (h/raw (:pandocir/text ir-node))))

(defmethod ir->hiccup-1 :pandocir.type/link [_ir-node]
  :pandocir.error/link-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/image [_ir-node]
  :pandocir.error/image-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/note [_ir-node]
  :pandocir.error/note-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/span [_ir-node]
  :pandocir.error/span-not-implemented)

;; Block
(defmethod ir->hiccup-1 :pandocir.type/plain [_ir-node]
  :pandocir.error/plain-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/para [_ir-node]
  :pandocir.error/para-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/line-block [_ir-node]
  :pandocir.error/line-block-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/code-block [_ir-node]
  :pandocir.error/code-block-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/raw-block [_ir-node]
  :pandocir.error/raw-block-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/block-quote [_ir-node]
  :pandocir.error/block-quote-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/ordered-list [_ir-node]
  :pandocir.error/ordered-list-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/bullet-list [_ir-node]
  :pandocir.error/bullet-list-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/definition-list [_ir-node]
  :pandocir.error/definition-list-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/header [_ir-node]
  :pandocir.error/header-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/horizontal-rule [_ir-node]
  :pandocir.error/horizontal-rule-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/table [_ir-node]
  :pandocir.error/table-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/figure [_ir-node]
  :pandocir.error/figure-not-implemented)
(defmethod ir->hiccup-1 :pandocir.type/div [_ir-node]
  :pandocir.error/div-not-implemented)

;; Arguments
(defmethod ir->hiccup-1 :pandocir/attr [_ir-node]
  :pandocir.error/not-implemented)
(defmethod ir->hiccup-1 :pandocir/list-attr [_ir-node]
  :pandocir.error/not-implemented)

(defmethod ir->hiccup-1 :default [x]
  x)

(defn ir->hiccup [ir]
  (walk/postwalk ir->hiccup-1 ir))
