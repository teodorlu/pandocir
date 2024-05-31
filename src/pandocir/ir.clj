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
   "Span"           [:pandocir.type/span :pandocir/attr :pandocir/inlines]

   ;; Block
   "Plain"          [:pandocir.type/plain :pandocir/inlines]
   "Para"           [:pandocir.type/para :pandocir/inlines]
   "LineBlock"      [:pandocir.type/line-block :pandocir/inlines]
   "CodeBlock"      [:pandocir.type/code-block :pandocir/attr :pandocir/text]
   "RawBlock"       [:pandocir.type/raw-block :pandocir/format :pandocir/text]
   "BlockQuote"     [:pandocir.type/block-quote :pandocir/blocks]
   "OrderedList"    [:pandocir.type/ordered-list :pandocir/list-attr :pandocir/list-items]
   "BulletList"     [:pandocir.type/bullet-list :pandocir/list-items]
   "DefinitionList" [:pandocir.type/definition-list :pandocir/definitions]
   "Header"         [:pandocir.type/header :pandocir/level :pandocir/attr :pandocir/inlines]
   "HorizontalRule" [:pandocir.type/horizontal-rule]
   "Table"          [:pandocir.type/table
                     :pandocir/attr
                     :pandocir.table/caption
                     :pandocir.table/col-specs
                     :pandocir.table/head
                     :pandocir.table/body
                     :pandocir.table/foot]
   "Figure"         [:pandocir.type/figure :pandocir/attr :pandocir.figure/caption :pandocir/blocks]
   "Div"            [:pandocir.type/div :pandocir/attr :pandocir/blocks]})


(defn ^:private attr->ir [[id classes keyvals]]
  {:pandocir.attr/id id
   :pandocir.attr/classes classes
   :pandocir.attr/keyvals (into {} keyvals)})

(defn ^:private list-attr->ir [[start style delim]]
  {:pandocir.list-attr/start start
   :pandocir.list-attr/style style
   :pandocir.list-attr/delim delim})

(defn ^:private pandoc->ir-1 [{:keys [t c] :as pandoc-node}]
  (if-let [[pandocir-type & args] (get pandoc-types t)]
    (cond-> {}
      (= 1 (count args)) (assoc (first args) c)
      (< 1 (count args)) (merge (zipmap args c))
      true (assoc :pandocir/type pandocir-type)
      ((set args) :pandocir/attr) (update :pandocir/attr attr->ir)
      ((set args) :pandocir/list-attr) (update :pandocir/list-attr list-attr->ir))
    pandoc-node))

(defn pandoc->ir [inline]
  (walk/postwalk pandoc->ir-1 inline))
