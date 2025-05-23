(ns pandocir.ir
  "A namespace for converting between Pandoc JSON abstract syntax trees and a
  Clojure intermediate representation (IR)."
  (:require
   [clojure.string :as s]
   [clojure.walk :as walk]))

(defn ^:private associate-by
  "Returns a map of the elements of coll keyed by the result of f on each
  element. Each element is assumed to be unique (if this is not the case,
  consider using [[group-by]])."
  [f coll]
  (reduce (fn [m v] (assoc m (f v) v)) {} coll))

(defn ^:private make-descriptor
  "Produces a Pandoc type descriptor. The pandoc-type is Pandoc's name for the AST
  node, which is converted to lisp-case and namespaced for pandocir. The
  children of the Pandoc AST node are named by args (in order)."
  [pandoc-type & args]
  (let [words (re-seq #"[A-Z][a-z]+" pandoc-type)
        lisp-cased (s/join "-" (map s/lower-case words))]
    {:pandocir/type (keyword "pandocir.type" lisp-cased)
     :pandocir/pandoc-type pandoc-type
     :pandocir/args args}))

(def ^:private pandoc-type-descriptors
  "A list of Pandoc type descriptors."
  (mapv (partial apply make-descriptor)
        [
         ;; Inline
         ["Str" :pandocir/text]
         ["Emph" :pandocir/inlines]
         ["Underline" :pandocir/inlines]
         ["Strong" :pandocir/inlines]
         ["Strikeout" :pandocir/inlines]
         ["Superscript" :pandocir/inlines]
         ["Subscript" :pandocir/inlines]
         ["SmallCaps" :pandocir/inlines]
         ["Quoted" :pandocir.quote/type :pandocir/inlines]
         ["Cite" :pandocir/citations :pandocir/inlines]
         ["Code" :pandocir/attr :pandocir/text]
         ["Space"]
         ["SoftBreak"]
         ["LineBreak"]
         ["Math" :pandocir.math/type :pandocir/text]
         ["RawInline" :pandocir/format :pandocir/text]
         ["Link" :pandocir/attr :pandocir/inlines :pandocir/link]
         ["Image" :pandocir/attr :pandocir/inlines :pandocir/image]
         ["Note" :pandocir/blocks]
         ["Span" :pandocir/attr :pandocir/inlines]

         ;; Block
         ["Plain" :pandocir/inlines]
         ["Para" :pandocir/inlines]
         ["LineBlock" :pandocir/inlines]
         ["CodeBlock" :pandocir/attr :pandocir/text]
         ["RawBlock" :pandocir/format :pandocir/text]
         ["BlockQuote" :pandocir/blocks]
         ["OrderedList" :pandocir/list-attr :pandocir/list-items]
         ["BulletList" :pandocir/list-items]
         ["DefinitionList" :pandocir/definitions]
         ["Header" :pandocir/level :pandocir/attr :pandocir/inlines]
         ["HorizontalRule"]
         ["Table" :pandocir/attr :pandocir/caption :pandocir.table/col-specs :pandocir.table/head :pandocir.table/body :pandocir.table/foot]
         ["Figure" :pandocir/attr :pandocir/caption :pandocir/blocks]
         ["Div" :pandocir/attr :pandocir/blocks]

         ;; Quotes
         ["SingleQuote"]
         ["DoubleQuote"]

         ;; List styles
         ["DefaultStyle"]
         ["Example"]
         ["Decimal"]
         ["LowerRoman"]
         ["UpperRoman"]
         ["LowerAlpha"]
         ["UpperAlpha"]

         ;; List delimiters
         ["DefaultDelim"]
         ["Period"]
         ["OneParen"]
         ["TwoParens"]

         ;; Citation modes
         ["AuthorInText"]
         ["SuppressAuthor"]
         ["NormalCitation"]]))

(def ^:private pandoc-arg-descriptor
  "Descriptions for arguments to Pandoc AST nodes."
  {:pandocir/attr [:pandocir.attr/id :pandocir.attr/classes :pandocir.attr/keyvals]
   :pandocir/list-attr [:pandocir.list-attr/start :pandocir.list-attr/style :pandocir.list-attr/delim]
   :pandocir/link [:pandocir.link/href :pandocir.link/title]
   :pandocir/image [:pandocir.image/src :pandocir.image/title]
   :pandocir/caption [:pandocir.caption/short :pandocir.caption/blocks]})

(def ^:private pandoc-simple-types
  "Pandoc AST nodes that are always leaves. In addition, these types are not
  inlines or blocks, meaning they only occur in some well-defined contexts."
  [:pandocir.quote/type :pandocir.list-attr/style :pandocir.list-attr/delim])

(def ^:private descriptors-by-pandoc-type
  "A mapping from the original Pandoc type to the corresponding type descriptor."
  (associate-by :pandocir/pandoc-type pandoc-type-descriptors))

(def ^:private descriptors-by-pandocir-type
  "A mapping from the pandocir type to the corresponding type descriptor."
  (associate-by :pandocir/type pandoc-type-descriptors))

(def ^:private record-fields-by-pandocir
  "A mapping from pandocir type to the original Pandoc record field."
  {:pandocir.citation/id :citationId
   :pandocir.citation/prefix :citationPrefix
   :pandocir.citation/suffix :citationSuffix
   :pandocir.citation/mode :citationMode
   :pandocir.citation/note-num :citationNoteNum
   :pandocir.citation/hash :citationHash})

(def ^:private record-fields-by-pandoc
  "A mapping from pandocir type to the original Pandoc record field."
  (reduce-kv (fn [m k v] (assoc m v k)) {} record-fields-by-pandocir))

(defn ^:private unwrap-simple-types
  "Transforms {k {:pandocir/type v}} into {k v} for every simple type listed
  in [[pandoc-simple-types]]. It is the inverse of [[wrap-simple-types]]."
  [ir-node]
  (-> (fn [node k]
        (cond-> node
          (k node) (assoc k (:pandocir/type (k node)))))
      (reduce ir-node pandoc-simple-types)))

(defn ^:private args->ir
  "Flattens `ir-node` whenever it contains keys that are also
  in [[pandoc-arg-descriptor]]. It is the inverse of [[ir->args]]."
  [ir-node]
  (-> (fn [node k]
        (if-let [args (k pandoc-arg-descriptor)]
          (-> (dissoc node k)
              (merge (zipmap args (k node))))
          node))
      (reduce ir-node (keys ir-node))))

(defn ^:private pandoc->ir-1
  "Converts a Pandoc JSON abstract syntax tree node `pandoc-node` to pandocir.
  It is the inverse of [[ir->pandoc-1]].

  Note that it only converts a single node of the tree. See [[pandoc->ir]] for
  converting a full Pandoc JSON abstract syntax tree."
  [{:keys [t c] :as pandoc-node}]
  (if-let [{:pandocir/keys [type args]} (get descriptors-by-pandoc-type t)]
    (unwrap-simple-types
     (args->ir
      (cond-> {:pandocir/type type}
        (= 1 (count args)) (assoc (first args) c)
        (< 1 (count args)) (merge (zipmap args c)))))
    (record-fields-by-pandoc pandoc-node pandoc-node)))

(defn ^:private wrap-simple-types
  "Transforms {k v} into {k {:pandocir/type v}} for every simple type listed
  in [[pandoc-simple-types]]. It is the inverse of [[unwrap-simple-types]]."
  [ir-node]
  (-> (fn [node k]
        (cond-> node
          (k node) (assoc k {:pandocir/type (k node)})))
      (reduce ir-node pandoc-simple-types)))

(defn ^:private ir->args
  "Inflates `ir-node` whenever it contains keys that are also
  in [[pandoc-arg-descriptor]]. It is the inverse of [[args->ir]]."
  [ir-node]
  (-> (fn [node [k args]]
        (if-let [vs (mapv node args)]
          (assoc node k vs)
          node))
      (reduce ir-node pandoc-arg-descriptor)))

(defn ^:private ir->pandoc-1
  "Converts a pandocir node `ir-node` to a Pandoc JSON abstract syntax tree node.
  It is the inverse of [[pandoc->ir-1]].

  Note that it only converts a single node of the tree. See [[ir->pandoc]] for
  converting a full pandocir tree."
  [{:pandocir/keys [type] :as ir-node}]
  (if-let [{:pandocir/keys [pandoc-type args]} (get descriptors-by-pandocir-type type)]
    (let [node (ir->args (wrap-simple-types ir-node))]
      (cond-> {:t pandoc-type}
        args (assoc :c ((apply juxt args) node))
        (= 1 (count args)) (update :c first)))
    (record-fields-by-pandocir ir-node ir-node)))

(defn pandoc->ir
  "Walk a Pandoc JSON abstract syntax tree and convert each node to a pandocir
  node by calling [[pandoc->ir-1]]. It is the inverse of [[ir->pandoc]]."
  [pandoc]
  (walk/postwalk pandoc->ir-1 pandoc))

(defn ir->pandoc
  "Walk a pandocir tree and convert each node to a Pandoc JSON abstract syntax
  tree node by calling [[ir->pandoc-1]]. It is the inverse of [[pandoc->ir]]."
  [ir]
  (walk/prewalk ir->pandoc-1 ir))
