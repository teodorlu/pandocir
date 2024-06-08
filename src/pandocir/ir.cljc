(ns pandocir.ir
  (:require [clojure.walk :as walk]))

(defn ^:private associate-by [f coll]
  (reduce (fn [m v] (assoc m (f v) v)) {} coll))

(def ^:private pandoc-types
  [
   ;; Inline
   {:pandocir/type :pandocir.type/str
    :pandocir/pandoc-type "Str"
    :pandocir/args [:pandocir/text]}
   {:pandocir/type :pandocir.type/emph
    :pandocir/pandoc-type "Emph"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/underline
    :pandocir/pandoc-type "Underline"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/strong
    :pandocir/pandoc-type "Strong"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/strikeout
    :pandocir/pandoc-type "Strikeout"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/superscript
    :pandocir/pandoc-type "Superscript"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/subscript
    :pandocir/pandoc-type "Subscript"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/small-caps
    :pandocir/pandoc-type "SmallCaps"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/quoted
    :pandocir/pandoc-type "Quoted"
    :pandocir/args [:pandocir.quote/type :pandocir/inlines]}
   {:pandocir/type :pandocir.type/cite
    :pandocir/pandoc-type "Cite"
    :pandocir/args [:pandocir/citations :pandocir/inlines]}
   {:pandocir/type :pandocir.type/code
    :pandocir/pandoc-type "Code"
    :pandocir/args [:pandocir/attr :pandocir/text]}
   {:pandocir/type :pandocir.type/space
    :pandocir/pandoc-type "Space"}
   {:pandocir/type :pandocir.type/soft-break
    :pandocir/pandoc-type "SoftBreak"}
   {:pandocir/type :pandocir.type/line-break
    :pandocir/pandoc-type "LineBreak"}
   {:pandocir/type :pandocir.type/math
    :pandocir/pandoc-type "Math"
    :pandocir/args [:pandocir.math/type :pandocir/text]}
   {:pandocir/type :pandocir.type/raw-inline
    :pandocir/pandoc-type "RawInline"
    :pandocir/args [:pandocir/format :pandocir/text]}
   {:pandocir/type :pandocir.type/link
    :pandocir/pandoc-type "Link"
    :pandocir/args [:pandocir/attr :pandocir/inlines :pandocir/link]}
   {:pandocir/type :pandocir.type/image
    :pandocir/pandoc-type "Image"
    :pandocir/args [:pandocir/attr :pandocir/inlines :pandocir/image]}
   {:pandocir/type :pandocir.type/note
    :pandocir/pandoc-type "Note"
    :pandocir/args [:pandocir/blocks]}
   {:pandocir/type :pandocir.type/span
    :pandocir/pandoc-type "Span"
    :pandocir/args [:pandocir/attr :pandocir/inlines]}

   ;; Block
   {:pandocir/type :pandocir.type/plain
    :pandocir/pandoc-type "Plain"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/para
    :pandocir/pandoc-type "Para"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/line-block
    :pandocir/pandoc-type "LineBlock"
    :pandocir/args [:pandocir/inlines]}
   {:pandocir/type :pandocir.type/code-block
    :pandocir/pandoc-type "CodeBlock"
    :pandocir/args [:pandocir/attr :pandocir/text]}
   {:pandocir/type :pandocir.type/raw-block
    :pandocir/pandoc-type "RawBlock"
    :pandocir/args [:pandocir/format :pandocir/text]}
   {:pandocir/type :pandocir.type/block-quote
    :pandocir/pandoc-type "BlockQuote"
    :pandocir/args [:pandocir/blocks]}
   {:pandocir/type :pandocir.type/ordered-list
    :pandocir/pandoc-type "OrderedList"
    :pandocir/args [:pandocir/list-attr :pandocir/list-items]}
   {:pandocir/type :pandocir.type/bullet-list
    :pandocir/pandoc-type "BulletList"
    :pandocir/args [:pandocir/list-items]}
   {:pandocir/type :pandocir.type/definition-list
    :pandocir/pandoc-type "DefinitionList"
    :pandocir/args [:pandocir/definitions]}
   {:pandocir/type :pandocir.type/header
    :pandocir/pandoc-type "Header"
    :pandocir/args [:pandocir/level :pandocir/attr :pandocir/inlines]}
   {:pandocir/type :pandocir.type/horizontal-rule
    :pandocir/pandoc-type "HorizontalRule"}
   {:pandocir/type :pandocir.type/table
    :pandocir/pandoc-type "Table"
    :pandocir/args [:pandocir/attr
                    :pandocir.table/caption
                    :pandocir.table/col-specs
                    :pandocir.table/head
                    :pandocir.table/body
                    :pandocir.table/foot]}
   {:pandocir/type :pandocir.type/figure
    :pandocir/pandoc-type "Figure"
    :pandocir/args [:pandocir/attr :pandocir.figure/caption :pandocir/blocks]}
   {:pandocir/type :pandocir.type/div
    :pandocir/pandoc-type "Div"
    :pandocir/args [:pandocir/attr :pandocir/blocks]}

   ;; Quotes
   {:pandocir/type :pandocir.type/single-quote
    :pandocir/pandoc-type "SingleQuote"}
   {:pandocir/type :pandocir.type/double-quote
    :pandocir/pandoc-type "DoubleQuote"}

   ;; List styles
   {:pandocir/type :pandocir.type/default-style
    :pandocir/pandoc-type "DefaultStyle"}
   {:pandocir/type :pandocir.type/example
    :pandocir/pandoc-type "Example"}
   {:pandocir/type :pandocir.type/decimal
    :pandocir/pandoc-type "Decimal"}
   {:pandocir/type :pandocir.type/lower-roman
    :pandocir/pandoc-type "LowerRoman"}
   {:pandocir/type :pandocir.type/upper-roman
    :pandocir/pandoc-type "UpperRoman"}
   {:pandocir/type :pandocir.type/lower-alpha
    :pandocir/pandoc-type "LowerAlpha"}
   {:pandocir/type :pandocir.type/upper-alpha
    :pandocir/pandoc-type "UpperAlpha"}

   ;; List delimiters
   {:pandocir/type :pandoc.type/default-delim
    :pandocir/pandoc-type "DefaultDelim"}
   {:pandocir/type :pandoc.type/period
    :pandocir/pandoc-type "Period"}
   {:pandocir/type :pandoc.type/one-paren
    :pandocir/pandoc-type "OneParen"}
   {:pandocir/type :pandoc.type/two-parens
    :pandocir/pandoc-type "TwoParens"}])

(def ^:private pandoc-args
  {:pandocir/attr [:pandocir.attr/id :pandocir.attr/classes :pandocir.attr/keyvals]
   :pandocir/list-attr [:pandocir.list-attr/start :pandocir.list-attr/style :pandocir.list-attr/delim]
   :pandocir/link [:pandocir.link/href :pandocir.link/title]
   :pandocir/image [:pandocir.image/src :pandocir.image/title]})

(def ^:private pandoc-simple-types
  [:pandocir.quote/type :pandocir.list-attr/style :pandocir.list-attr/delim])

(def ^:private pandoc-types-by-pandoc-type
  (associate-by :pandocir/pandoc-type pandoc-types))

(def ^:private pandoc-types-by-pandocir-type
  (associate-by :pandocir/type pandoc-types))

(defn ^:private unwrap-simple-types [ir-node]
  (-> (fn [node k]
        (cond-> node
          (k node) (assoc k (:pandocir/type (k node)))))
      (reduce ir-node pandoc-simple-types)))

(defn ^:private args->ir [ir-node]
  (-> (fn [node k]
        (if-let [args (k pandoc-args)]
          (-> (dissoc node k)
              (merge (zipmap args (k node))))
          node))
      (reduce ir-node (keys ir-node))))

(defn ^:private pandoc->ir-1 [{:keys [t c] :as pandoc-node}]
  (if-let [{:pandocir/keys [type args]} (get pandoc-types-by-pandoc-type t)]
    (unwrap-simple-types
     (args->ir
      (cond-> {:pandocir/type type}
        (= 1 (count args)) (assoc (first args) c)
        (< 1 (count args)) (merge (zipmap args c)))))
    pandoc-node))

(defn ^:private wrap-simple-types [ir-node]
  (-> (fn [node k]
        (cond-> node
          (k node) (assoc k {:pandocir/type (k node)})))
      (reduce ir-node pandoc-simple-types)))

(defn ^:private ir->args [ir-node]
  (-> (fn [node [k args]]
        (if-let [vs (mapv node args)]
          (assoc node k vs)
          node))
      (reduce ir-node pandoc-args)))

(defn ^:private ir->pandoc-1 [{:pandocir/keys [type] :as ir-node}]
  (if-let [{:pandocir/keys [pandoc-type args]} (get pandoc-types-by-pandocir-type type)]
    (let [node (ir->args (wrap-simple-types ir-node))]
      (cond-> {:t pandoc-type}
        args (assoc :c ((apply juxt args) node))
        (= 1 (count args)) (update :c first)))
    ir-node))

(defn pandoc->ir [pandoc]
  (walk/postwalk pandoc->ir-1 pandoc))

(defn ir->pandoc [ir]
  (walk/prewalk ir->pandoc-1 ir))
