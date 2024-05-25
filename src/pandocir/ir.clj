(ns pandocir.ir
  (:require [clojure.walk :as walk]))

(def ^:private pandoc-types
  {"Str"            [:pandocir.type/str :pandocir/text]
   "Emph"           [:pandocir.type/emph :pandocir/inlines]
   "Underline"      [:pandocir.type/underline :pandocir/inlines]
   "Strong"         [:pandocir.type/strong :pandocir/inlines]
   "Strikeout"      [:pandocir.type/strikeout :pandocir/inlines]
   "Superscript"    [:pandocir.type/superscript :pandocir/inlines]
   "Subscript"      [:pandocir.type/subscript :pandocir/inlines]
   "SmallCaps"      [:pandocir.type/small-caps :pandocir/inlines]
   "Quoted"         [:pandocir.type/quoted :pandocir.quote/type :pandocir/inlines]
   "Cite"           [:pandocir.type/cite :pandocir/citations :pandocir/inlines]
   "Code"           [:pandocir.type/code :pandocir/attr :pandocir/text]
   "Space"          [:pandocir.type/space]
   "SoftBreak"      [:pandocir.type/soft-break]
   "LineBreak"      [:pandocir.type/line-break]
   "Math"           [:pandocir.type/math :pandocir.math/type :pandocir/text]
   "RawInline"      [:pandocir.type/raw-inline :pandocir/format :pandocir/text]
   "Link"           [:pandocir.type/link :pandocir/attr :pandocir/inlines :pandocir/target]
   "Image"          [:pandocir.type/image :pandocir/attr :pandocir/inlines :pandocir/target]
   "Note"           [:pandocir.type/note :pandocir/blocks]
   "Span"           [:pandocir.type/span :pandocir/attr :pandocir/inlines]})

(defn ^:private pandoc->ir-1 [{:keys [t c] :as x}]
  (if-let [[pandocir-type & args] (get pandoc-types t)]
    (merge {:pandocir/type pandocir-type}
           (cond (nil? c) {}
                 (vector? c) (zipmap args c)
                 :else {(first args) c}))
    x))

(defn pandoc->ir [inline]
  (walk/postwalk pandoc->ir-1 inline))
