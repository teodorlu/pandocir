(ns pandocir.type
  "Pandoc types without setting your hair on fire

  Pandoc's types are from this package:

    https://hackage.haskell.org/package/pandoc-types"
  (:refer-clojure :exclude [str type]))



(defn type [block-or-inline]
  (:t block-or-inline))

(def emph "Emph")
(def quoted "Quoted")
(def smallcaps "SmallCaps")
(def space "Space")
(def str "Str")
(def strikeout "Strikeout")
(def strong "Strong")
(def subscript "Subscript")
(def superscript "Superscript")

(comment
  (require '[clojure.string])

  ;; Code generation for defs in this namespace
  (for [kw (sort [:Space
                  :Str
                  :Emph
                  :Strong
                  :Strikeout
                  :Superscript
                  :Subscript
                  :SmallCaps
                  :Quoted])]
    (list 'def
          (symbol (clojure.string/lower-case (name kw)))
          (name kw)))

  ;; Code generation for predicate functions in `pandocir.core`
  (for [kw (sort [:Space
                  :Str
                  :Emph
                  :Strong
                  :Strikeout
                  :Superscript
                  :Subscript
                  :SmallCaps
                  :Quoted])]
    (list
     'defn
     #_
     (clojure.core/str (clojure.string/lower-case (name kw))
                       "?")
     (symbol (clojure.core/str (clojure.string/lower-case (name kw))
                               "?"))
     '[block-or-inline]
     (list '= (symbol "type" (clojure.string/lower-case (name kw)))
           (list :t 'block-or-inline))))

  :rcf)
