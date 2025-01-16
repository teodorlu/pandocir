(ns pandocir.hiccup
  "A namespace for converting from pandocir to hiccup."
  (:require [clojure.walk :as walk]
            #?(:clj [hiccup2.core :as h])
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
(defmethod ir->hiccup-1 :pandocir.type/str [ir-node state]
  (:pandocir/text ir-node))

(defmethod ir->hiccup-1 :pandocir.type/emph [ir-node state]
  (into [:em] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/underline [ir-node state]
  (into [:u] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/strong [ir-node state]
  (into [:strong] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/strikeout [ir-node state]
  (into [:del] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/superscript [ir-node state]
  (into [:sup] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/subscript [ir-node state]
  (into [:sub] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/small-caps [ir-node state]
  (into [:span {:class ["smallcaps"]}] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/quoted [ir-node state]
  (let [[open close] (case (:pandocir.quote/type ir-node)
                       :pandocir.type/single-quote ["‘" "’"]
                       :pandocir.type/double-quote ["“" "”"])]
    (concat [open] (:pandocir/inlines ir-node) [close])))

(defmethod ir->hiccup-1 :pandocir.type/cite [_ir-node state]
  :pandocir.error/cite-not-implemented)

(defmethod ir->hiccup-1 :pandocir.type/code [ir-node state]
  [:code (ir->html-attrs ir-node) (:pandocir/text ir-node)])

(defmethod ir->hiccup-1 :pandocir.type/space [_ir-node state]
  " ")

(defmethod ir->hiccup-1 :pandocir.type/soft-break [_ir-node state]
  " ")

(defmethod ir->hiccup-1 :pandocir.type/line-break [_ir-node state]
  [:br])

(defmethod ir->hiccup-1 :pandocir.type/math [_ir-node state]
  :pandocir.error/math-not-implemented)

(defmethod ir->hiccup-1 :pandocir.type/raw-inline [ir-node state]
  ;; Raw strings are treated differently in hiccup, reagent and replicant.
  ;; This solution is dependent on hiccup, which might not be ideal.
  (when (= (:pandocir/format ir-node) "html")
    #?(:clj (h/raw (:pandocir/text ir-node))
       :cljs (:pandocir/text ir-node))))

(defmethod ir->hiccup-1 :pandocir.type/link [ir-node state]
  (into [:a (ir->html-attrs ir-node)] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/image [ir-node state]
  [:img (assoc (ir->html-attrs ir-node)
               :alt (->> (:pandocir/inlines ir-node)
                         (map #(ir->hiccup-1 % state))
                         s/join))])

(defmethod ir->hiccup-1 :pandocir.type/note [_ir-node state]
  :pandocir.error/note-not-implemented)

(defmethod ir->hiccup-1 :pandocir.type/span [ir-node state]
  (into [:span (ir->html-attrs ir-node)] (:pandocir/inlines ir-node)))

;; Block
(defmethod ir->hiccup-1 :pandocir.type/plain [ir-node state]
  (seq (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/para [ir-node state]
  (into [:p] (:pandocir/inlines ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/line-block [ir-node state]
  (into [:div {:class ["line-block"]}]
        (mapcat identity (interpose (list [:br] "\n") (:pandocir/inlines ir-node)))))

(defmethod ir->hiccup-1 :pandocir.type/code-block [ir-node state]
  [:pre (ir->html-attrs ir-node) [:code (:pandocir/text ir-node)]])

(defmethod ir->hiccup-1 :pandocir.type/raw-block [ir-node state]
  ;; Raw strings are treated differently in hiccup, reagent and replicant.
  ;; This solution is dependent on hiccup, which might not be ideal.
  (when (= (:pandocir/format ir-node) "html")
    #?(:clj (h/raw (:pandocir/text ir-node))
       :cljs (:pandocir/text ir-node))))

(defmethod ir->hiccup-1 :pandocir.type/block-quote [ir-node state]
  (into [:blockquote] (:pandocir/blocks ir-node)))

(defmethod ir->hiccup-1 :pandocir.type/ordered-list [ir-node state]
  (let [{:pandocir.list-attr/keys [start style]} ir-node
        type (case style
               :pandocir.type/lower-roman "i"
               :pandocir.type/upper-roman "I"
               :pandocir.type/lower-alpha "a"
               :pandocir.type/upper-alpha "A"
               "1")]
    (into [:ol (cond-> {:type type}
                 (not= start 1) (assoc :start start))]
          (map (partial into [:li]) (:pandocir/list-items ir-node)))))

(defmethod ir->hiccup-1 :pandocir.type/bullet-list [ir-node state]
  (into [:ul] (map (partial into [:li]) (:pandocir/list-items ir-node))))

(defmethod ir->hiccup-1 :pandocir.type/definition-list [ir-node state]
  (->> (:pandocir/definitions ir-node)
       (mapcat (fn [[term definition]]
                 (->> definition
                      (map (partial into [:dd]))
                      (cons (into [:dt] term)))))
       (into [:dl])))

(defmethod ir->hiccup-1 :pandocir.type/header [ir-node state]
  (let [h (keyword (str "h" (:pandocir/level ir-node)))]
    (into [h (ir->html-attrs ir-node)] (:pandocir/inlines ir-node))))

(defmethod ir->hiccup-1 :pandocir.type/horizontal-rule [_ state]
  [:hr])

(defmethod ir->hiccup-1 :pandocir.type/table [_ir-node state]
  :pandocir.error/table-not-implemented)

(defmethod ir->hiccup-1 :pandocir.type/figure [ir-node state]
  (conj (into [:figure (ir->html-attrs ir-node)] (:pandocir/blocks ir-node))
        (into [:figcaption] (:pandocir.caption/blocks ir-node))))

(defmethod ir->hiccup-1 :pandocir.type/div [ir-node state]
  (into [:div (ir->html-attrs ir-node)] (:pandocir/blocks ir-node)))

(defmethod ir->hiccup-1 :default [x state]
  x)

(defn ir->hiccup [ir]
  (let [state (atom {})]
    (walk/postwalk #(ir->hiccup-1 % state) ir)))
