(ns pandocir.hiccup)

(defmulti ir->hiccup :pandocir/type)

;; Inline
(defmethod ir->hiccup :pandocir.type/str [ir-node]
  :pandocir.error/str-not-implemented)
(defmethod ir->hiccup :pandocir.type/emph [ir-node]
  :pandocir.error/emph-not-implemented)
(defmethod ir->hiccup :pandocir.type/underline [ir-node]
  :pandocir.error/underline-not-implemented)
(defmethod ir->hiccup :pandocir.type/strong [ir-node]
  :pandocir.error/strong-not-implemented)
(defmethod ir->hiccup :pandocir.type/strikeout [ir-node]
  :pandocir.error/strikeout-not-implemented)
(defmethod ir->hiccup :pandocir.type/superscript [ir-node]
  :pandocir.error/superscript-not-implemented)
(defmethod ir->hiccup :pandocir.type/subscript [ir-node]
  :pandocir.error/subscript-not-implemented)
(defmethod ir->hiccup :pandocir.type/small-caps [ir-node]
  :pandocir.error/small-caps-not-implemented)
(defmethod ir->hiccup :pandocir.type/quoted [ir-node]
  :pandocir.error/quoted-not-implemented)
(defmethod ir->hiccup :pandocir.type/cite [ir-node]
  :pandocir.error/cite-not-implemented)
(defmethod ir->hiccup :pandocir.type/code [ir-node]
  :pandocir.error/code-not-implemented)
(defmethod ir->hiccup :pandocir.type/space [ir-node]
  :pandocir.error/space-not-implemented)
(defmethod ir->hiccup :pandocir.type/soft-break [ir-node]
  :pandocir.error/soft-break-not-implemented)
(defmethod ir->hiccup :pandocir.type/line-break [ir-node]
  :pandocir.error/line-break-not-implemented)
(defmethod ir->hiccup :pandocir.type/math [ir-node]
  :pandocir.error/math-not-implemented)
(defmethod ir->hiccup :pandocir.type/raw-inline [ir-node]
  :pandocir.error/raw-inline-not-implemented)
(defmethod ir->hiccup :pandocir.type/link [ir-node]
  :pandocir.error/link-not-implemented)
(defmethod ir->hiccup :pandocir.type/image [ir-node]
  :pandocir.error/image-not-implemented)
(defmethod ir->hiccup :pandocir.type/note [ir-node]
  :pandocir.error/note-not-implemented)
(defmethod ir->hiccup :pandocir.type/span [ir-node]
  :pandocir.error/span-not-implemented)

;; Block
(defmethod ir->hiccup :pandocir.type/plain [ir-node]
  :pandocir.error/plain-not-implemented)
(defmethod ir->hiccup :pandocir.type/para [ir-node]
  :pandocir.error/para-not-implemented)
(defmethod ir->hiccup :pandocir.type/line-block [ir-node]
  :pandocir.error/line-block-not-implemented)
(defmethod ir->hiccup :pandocir.type/code-block [ir-node]
  :pandocir.error/code-block-not-implemented)
(defmethod ir->hiccup :pandocir.type/raw-block [ir-node]
  :pandocir.error/raw-block-not-implemented)
(defmethod ir->hiccup :pandocir.type/block-quote [ir-node]
  :pandocir.error/block-quote-not-implemented)
(defmethod ir->hiccup :pandocir.type/ordered-list [ir-node]
  :pandocir.error/ordered-list-not-implemented)
(defmethod ir->hiccup :pandocir.type/bullet-list [ir-node]
  :pandocir.error/bullet-list-not-implemented)
(defmethod ir->hiccup :pandocir.type/definition-list [ir-node]
  :pandocir.error/definition-list-not-implemented)
(defmethod ir->hiccup :pandocir.type/header [ir-node]
  :pandocir.error/header-not-implemented)
(defmethod ir->hiccup :pandocir.type/horizontal-rule [ir-node]
  :pandocir.error/horizontal-rule-not-implemented)
(defmethod ir->hiccup :pandocir.type/table [ir-node]
  :pandocir.error/table-not-implemented)
(defmethod ir->hiccup :pandocir.type/figure [ir-node]
  :pandocir.error/figure-not-implemented)
(defmethod ir->hiccup :pandocir.type/div [ir-node]
  :pandocir.error/div-not-implemented)

;; Arguments
(defmethod ir->hiccup :pandocir/attr [ir-node]
  :pandocir.error/not-implemented)
(defmethod ir->hiccup :pandocir/list-attr [ir-node]
  :pandocir.error/not-implemented)

(defmethod ir->hiccup :default [_]
  :pandocir.error/not-implemented)
