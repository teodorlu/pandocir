(ns pandocir.type
  "Pandoc types without setting your hair on fire

  Pandoc's types are from this package:

    https://hackage.haskell.org/package/pandoc-types"
  (:refer-clojure :exclude [str type]))

;; Inline types
(def emph "Emph")
(def quoted "Quoted")
(def small-caps "SmallCaps")
(def space "Space")
(def str "Str")
(def strikeout "Strikeout")
(def strong "Strong")
(def subscript "Subscript")
(def superscript "Superscript")

(def inline-types
  #{emph strikeout superscript space small-caps strong subscript str quoted})

;; Block types
(def block-quote "BlockQuote")
(def bullet-list "BulletList")
(def code-block "CodeBlock")
(def header "Header")
(def ordered-list "OrderedList")
(def para "Para")
(def plain "Plain")

(def block-types
  #{bullet-list ordered-list block-quote code-block para header plain})

(comment
  (require '[clojure.string])
  (require '[clojure.pprint])

  (def inline-types-as-keywords [:Space :Str :Emph :Strong :Strikeout :Superscript :Subscript :SmallCaps :Quoted])
  (def block-types-as-keywords [:Plain :Para :Header :BlockQuote :CodeBlock :OrderedList :BulletList])

  (defn codegen-defs [types-as-keywords]
    (for [kw (sort types-as-keywords)]
      (list 'def
            (symbol (clojure.string/lower-case (name kw)))
            (name kw))))

  (defmacro pprint-code [arg]
    `(clojure.pprint/with-pprint-dispatch clojure.pprint/code-dispatch
       (clojure.pprint/pprint ~arg)))

  (pprint-code (codegen-defs inline-types-as-keywords))
  (pprint-code (codegen-defs block-types-as-keywords))

  `(def ~'inline-types ~(->> inline-types-as-keywords
                             (map name)
                             (map clojure.string/lower-case)
                             (map symbol)
                             (into #{})))

  `(def ~'block-types ~(->> block-types-as-keywords
                            (map name)
                            (map clojure.string/lower-case)
                            (map symbol)
                            (into #{})))

  (defn codegen-predicate-fns [types-as-keywords]
    (concat '(do)
            (for [kw (sort types-as-keywords)]
              (list
               'defn
               (symbol (clojure.core/str (clojure.string/lower-case (name kw))
                                         "?"))
               '[block-or-inline]
               (list '= (symbol "pandocir.type" (clojure.string/lower-case (name kw)))
                     (list 'pandocir.core/type 'block-or-inline))))))

  (pprint-code
   (codegen-predicate-fns inline-types-as-keywords))

  (pprint-code
   (codegen-predicate-fns block-types-as-keywords))


  :rcf)
