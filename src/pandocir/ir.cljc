(ns pandocir.ir
  "A namespace for converting between Pandoc JSON abstract syntax trees, to a
  Clojure intermediate representation."
  (:require
   [clojure.string :as s]
   [clojure.walk :as walk]))

(defn ^:private associate-by
  "Returns a map of the elements of coll keyed by the result of f on each
  element. Each element is assumed to be unique (if this is not the case,
  consider using [[group-by]])."
  [f coll]
  (reduce (fn [m v] (assoc m (f v) v)) {} coll))

(defn ^:private pandoc-type-desc
  "Produces Pandoc type descriptor. The pandoc-type is Pandoc's name for the AST
  node, which is converted to lisp-case and namespaced for pandocir. The
  children of the Pandoc AST node are named by args (in order)."
  [pandoc-type & args]
  (let [words (re-seq #"[A-Z][a-z]+" pandoc-type)
        lisp-cased (s/join "-" (map s/lower-case words))]
    {:pandocir/type (keyword "pandocir.type" lisp-cased)
     :pandocir/pandoc-type pandoc-type
     :pandocir/args args}))

(def ^:private pandoc-types
  "A list of Pandoc type descriptiors."
  [
   ;; Inline
   (pandoc-type-desc "Str" :pandocir/text)
   (pandoc-type-desc "Emph" :pandocir/inlines)
   (pandoc-type-desc "Underline" :pandocir/inlines)
   (pandoc-type-desc "Strong" :pandocir/inlines)
   (pandoc-type-desc "Strikeout" :pandocir/inlines)
   (pandoc-type-desc "Superscript" :pandocir/inlines)
   (pandoc-type-desc "Subscript" :pandocir/inlines)
   (pandoc-type-desc "SmallCaps" :pandocir/inlines)
   (pandoc-type-desc "Quoted" :pandocir.quote/type :pandocir/inlines)
   (pandoc-type-desc "Cite" :pandocir/citations :pandocir/inlines)
   (pandoc-type-desc "Code" :pandocir/attr :pandocir/text)
   (pandoc-type-desc "Space")
   (pandoc-type-desc "SoftBreak")
   (pandoc-type-desc "LineBreak")
   (pandoc-type-desc "Math" :pandocir.math/type :pandocir/text)
   (pandoc-type-desc "RawInline" :pandocir/format :pandocir/text)
   (pandoc-type-desc "Link" :pandocir/attr :pandocir/inlines :pandocir/link)
   (pandoc-type-desc "Image" :pandocir/attr :pandocir/inlines :pandocir/image)
   (pandoc-type-desc "Note" :pandocir/blocks)
   (pandoc-type-desc "Span" :pandocir/attr :pandocir/inlines)

   ;; Block
   (pandoc-type-desc "Plain" :pandocir/inlines)
   (pandoc-type-desc "Para" :pandocir/inlines)
   (pandoc-type-desc "LineBlock" :pandocir/inlines)
   (pandoc-type-desc "CodeBlock" :pandocir/attr :pandocir/text)
   (pandoc-type-desc "RawBlock" :pandocir/format :pandocir/text)
   (pandoc-type-desc "BlockQuote" :pandocir/blocks)
   (pandoc-type-desc "OrderedList" :pandocir/list-attr :pandocir/list-items)
   (pandoc-type-desc "BulletList" :pandocir/list-items)
   (pandoc-type-desc "DefinitionList" :pandocir/definitions)
   (pandoc-type-desc "Header" :pandocir/level :pandocir/attr :pandocir/inlines)
   (pandoc-type-desc "HorizontalRule")
   (pandoc-type-desc "Table" :pandocir/attr :pandocir.table/caption :pandocir.table/col-specs :pandocir.table/head :pandocir.table/body :pandocir.table/foot)
   (pandoc-type-desc "Figure" :pandocir/attr :pandocir.figure/caption :pandocir/blocks)
   (pandoc-type-desc "Div" :pandocir/attr :pandocir/blocks)

   ;; Quotes
   (pandoc-type-desc "SingleQuote")
   (pandoc-type-desc "DoubleQuote")

   ;; List styles
   (pandoc-type-desc "DefaultStyle")
   (pandoc-type-desc "Example")
   (pandoc-type-desc "Decimal")
   (pandoc-type-desc "LowerRoman")
   (pandoc-type-desc "UpperRoman")
   (pandoc-type-desc "LowerAlpha")
   (pandoc-type-desc "UpperAlpha")

   ;; List delimiters
   (pandoc-type-desc "DefaultDelim")
   (pandoc-type-desc "Period")
   (pandoc-type-desc "OneParen")
   (pandoc-type-desc "TwoParens")])

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
