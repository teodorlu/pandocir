(ns pandocir.ir
  (:require [clojure.walk :as walk]))

(defn- inline->ir-1 [{:keys [t c] :as thing}]
  (case t
    "Str"                               ; Str Text
    {:pandocir/type :pandocir.type/str :pandocir/text c}
    "Emph"                              ; Emph [Inline]
    {:pandocir/type :pandocir.type/emph :pandocir/inlines c}
    "Underline"                         ; Underline [Inline]
    {:pandocir/type :pandocir.type/underline :pandocir/inlines c}
    "Strong"                            ; Strong [Inline]
    {:pandocir/type :pandocir.type/strong :pandocir/inlines c}
    "Strikeout"                         ; Strikeout [Inline]
    {:pandocir/type :pandocir.type/strikeout :pandocir/inlines c}
    "Superscript"                       ; Superscript [Inline]
    {:pandocir/type :pandocir.type/superscript :pandocir/inlines c}
    "Subscript"                         ; Subscript [Inline]
    {:pandocir/type :pandocir.type/subscript :pandocir/inlines c}
    "SmallCaps"                         ; SmallCaps [Inline]
    {:pandocir/type :pandocir.type/small-caps :pandocir/inlines c}
    "Quoted"                            ; Quoted QuoteType [Inline]
    {:pandocir/type :pandocir.type/quoted
     :pandocir.quote/type (first c)
     :pandocir/inlines (second c)}
    "Cite"                              ; Cite [Citation] [Inline]
    {:pandocir/type :pandocir.type/cite
     :pandocir/citations (first c)
     :pandocir/inlines (second c)}
    "Code"                              ; Code Attr Text
    {:pandocir/type :pandocir.type/code
     :pandocir/attr (first c)
     :pandocir/text (second c)}
    "Space"                             ; Space
    {:pandocir/type :pandocir.type/space}
    "SoftBreak"                         ; SoftBreak
    {:pandocir/type :pandocir.type/soft-break}
    "LineBreak"                         ; LineBreak
    {:pandocir/type :pandocir.type/line-break}
    "Math"                              ; Math MathType Text
    {:pandocir/type :pandocir.type/math
     :pandocir.math/type (first c)
     :pandocir/text (second c)}
    "RawInline"                         ; RawInline Format Text
    {:pandocir/type :pandocir.type/raw-inline
     :pandocir/format (first c)
     :pandocir/text (second c)}
    "Link"                              ; Link Attr [Inline] Target
    {:pandocir/type :pandocir.type/link
     :pandocir/attr (first c)
     :pandocir/inlines (second c)
     :pandocir/target (last c)}
    "Image"                             ; Image Attr [Inline] Target
    {:pandocir/type :pandocir.type/image
     :pandocir/attr (first c)
     :pandocir/inlines (second c)
     :pandocir/target (last c)}
    "Note"                              ; Note [Block]
    {:pandocir/type :pandocir.type/note :pandocir/blocks c}
    "Span"                              ; Span Attr [Inline]
    {:pandocir/type :pandocir.type/span
     :pandocir/attr (first c)
     :pandocir/inlines (second c)}
    ;; This was not of type inline
    thing))

(defn inline->ir [inline]
  (walk/postwalk inline->ir-1 inline))
