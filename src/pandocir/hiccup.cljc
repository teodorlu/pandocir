(ns pandocir.hiccup
  (:require [clojure.walk :as walk]
            [hiccup2.core :as h]
            [clojure.string :as s]))

(defn ir->html-attrs [ir-node]
  (let [renaming {:pandocir.attr/id :id
                  :pandocir.attr/classes :class
                  :pandocir.link/href :href
                  :pandocir.link/title :title
                  :pandocir.image/src :src
                  :pandocir.image/title :title}
        keyvals (:pandocir.attr/keyvals ir-node)
        ks (map (comp keyword (partial str "data-") first) keyvals)]
    (-> (fn [m k]
          (let [v (k ir-node)]
            (cond-> m
              (seq v) (assoc (k renaming) v))))
        (reduce {} (keys renaming))
        (merge (zipmap ks (map second keyvals))))))

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
  [:code (ir->html-attrs ir-node) (:pandocir/text ir-node)])1

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

(defmethod ir->hiccup-1 :pandocir.type/link [ir-node]
  (into [:a (ir->html-attrs ir-node)] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/image [ir-node]
  (let [stringified (s/join (map ir->hiccup-1 (:pandocir/inlines ir-node)))]
    [:img (assoc (ir->html-attrs ir-node) :alt stringified)]))

(defmethod ir->hiccup-1 :pandocir.type/note [_ir-node]
  :pandocir.error/note-not-implemented)

(defmethod ir->hiccup-1 :pandocir.type/span [ir-node]
  (into [:span (ir->html-attrs ir-node)] (:pandocir/inlines ir-node)))

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
