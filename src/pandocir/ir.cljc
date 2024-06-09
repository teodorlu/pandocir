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

(defn ^:private make-pandoc-type [pandoc-type & args]
  (let [words (re-seq #"[A-Z][a-z]+" pandoc-type)
        kebab-cased (s/join "-" (map s/lower-case words))]
    {:pandocir/type (keyword "pandocir.type" kebab-cased)
     :pandocir/pandoc-type pandoc-type
     :pandocir/args args}))

(def ^:private pandoc-types
  [
   ;; Inline
   (make-pandoc-type "Str" :pandocir/text)
   (make-pandoc-type "Emph" :pandocir/inlines)
   (make-pandoc-type "Underline" :pandocir/inlines)
   (make-pandoc-type "Strong" :pandocir/inlines)
   (make-pandoc-type "Strikeout" :pandocir/inlines)
   (make-pandoc-type "Superscript" :pandocir/inlines)
   (make-pandoc-type "Subscript" :pandocir/inlines)
   (make-pandoc-type "SmallCaps" :pandocir/inlines)
   (make-pandoc-type "Quoted" :pandocir.quote/type :pandocir/inlines)
   (make-pandoc-type "Cite" :pandocir/citations :pandocir/inlines)
   (make-pandoc-type "Code" :pandocir/attr :pandocir/text)
   (make-pandoc-type "Space")
   (make-pandoc-type "SoftBreak")
   (make-pandoc-type "LineBreak")
   (make-pandoc-type "Math" :pandocir.math/type :pandocir/text)
   (make-pandoc-type "RawInline" :pandocir/format :pandocir/text)
   (make-pandoc-type "Link" :pandocir/attr :pandocir/inlines :pandocir/link)
   (make-pandoc-type "Image" :pandocir/attr :pandocir/inlines :pandocir/image)
   (make-pandoc-type "Note" :pandocir/blocks)
   (make-pandoc-type "Span" :pandocir/attr :pandocir/inlines)

   ;; Block
   (make-pandoc-type "Plain" :pandocir/inlines)
   (make-pandoc-type "Para" :pandocir/inlines)
   (make-pandoc-type "LineBlock" :pandocir/inlines)
   (make-pandoc-type "CodeBlock" :pandocir/attr :pandocir/text)
   (make-pandoc-type "RawBlock" :pandocir/format :pandocir/text)
   (make-pandoc-type "BlockQuote" :pandocir/blocks)
   (make-pandoc-type "OrderedList" :pandocir/list-attr :pandocir/list-items)
   (make-pandoc-type "BulletList" :pandocir/list-items)
   (make-pandoc-type "DefinitionList" :pandocir/definitions)
   (make-pandoc-type "Header" :pandocir/level :pandocir/attr :pandocir/inlines)
   (make-pandoc-type "HorizontalRule")
   (make-pandoc-type "Table" :pandocir/attr :pandocir.table/caption :pandocir.table/col-specs :pandocir.table/head :pandocir.table/body :pandocir.table/foot)
   (make-pandoc-type "Figure" :pandocir/attr :pandocir.figure/caption :pandocir/blocks)
   (make-pandoc-type "Div" :pandocir/attr :pandocir/blocks)

   ;; Quotes
   (make-pandoc-type "SingleQuote")
   (make-pandoc-type "DoubleQuote")

   ;; List styles
   (make-pandoc-type "DefaultStyle")
   (make-pandoc-type "Example")
   (make-pandoc-type "Decimal")
   (make-pandoc-type "LowerRoman")
   (make-pandoc-type "UpperRoman")
   (make-pandoc-type "LowerAlpha")
   (make-pandoc-type "UpperAlpha")

   ;; List delimiters
   (make-pandoc-type "DefaultDelim")
   (make-pandoc-type "Period")
   (make-pandoc-type "OneParen")
   (make-pandoc-type "TwoParens")])

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
