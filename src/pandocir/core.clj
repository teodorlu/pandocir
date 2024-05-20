(ns pandocir.core
  (:require
   [cheshire.core :as json]
   [pandocir.type])
  (:refer-clojure :exclude [type]))

(declare pandoc-inline->hiccup)
(declare pandoc-block->hiccup)

;; See: https://hackage.haskell.org/package/pandoc-types-1.23.1/docs/Text-Pandoc-Definition.html#t:Attr
(defn pandoc-attr->hiccup [[id classes keyvals]]
  (cond-> (into {} keyvals)
    (seq id) (assoc :id id)
    (seq classes) (assoc :class classes)))

(defn wrap-inline [wrapper content]
  (into wrapper (map pandoc-inline->hiccup content)))

(defn wrap-block [wrapper content]
  (into wrapper (map pandoc-block->hiccup content)))

;; See: https://hackage.haskell.org/package/pandoc-types-1.23.1/docs/Text-Pandoc-Definition.html#t:Inline
(defn pandoc-inline->hiccup [{:keys [t c]}]
  (case (keyword t)
    :Space " "
    :Str c
    :Emph (wrap-inline [:em] c)
    :Strong (wrap-inline [:strong] c)
    :Strikeout (wrap-inline [:del] c)
    :Superscript (wrap-inline [:sup] c)
    :Subscript (wrap-inline [:sub] c)
    ;; Pandoc defaults to generating a span with the `smallcaps` class; this
    ;; however assumes the pandoc default css file to be included. I'm opting to
    ;; inline this instead.
    ;; See: https://pandoc.org/chunkedhtml-demo/8.12-inline-formatting.html#small-caps
    :SmallCaps (wrap-inline [:span {:style {:font-variant "small-caps"}}] c)
    :Quoted (let [[{quote-type :t} content] c
                  [open close] (case (keyword quote-type)
                                 :SingleQuote ["‘" "’"]
                                 :DoubleQuote ["“" "”"])]
              (concat [open] (wrap-inline () content) [close]))))

(defn pandoc-header->hiccup [[level attr inlines]]
  (let [attributes (pandoc-attr->hiccup attr)]
    (cond-> [(keyword (str "h" level))]
      (seq  attributes) (conj attributes)
      true (into (map pandoc-inline->hiccup inlines)))))

(defn pandoc-codeblock->hiccup [[attr code]]
  (let [attributes (-> attr
                       (update 2 (partial map (fn [[k v]] [(str "data-" k) v])))
                       (pandoc-attr->hiccup))]
    (cond-> [:pre]
      (seq attributes) (conj attributes)
      true (conj [:code code]))))

;; See: https://hackage.haskell.org/package/pandoc-types-1.23.1/docs/Text-Pandoc-Definition.html#t:ListAttributes
(defn pandoc-list-attr->hiccup [[start list-style _]]
  (cond-> {}
    (and start (not= start 1)) (assoc :start start)
    list-style (assoc :type (case (keyword list-style)
                              :LowerAlpha "a"
                              :UpperAlpha "A"
                              :LowerRoman "i"
                              :UpperRoman "I"
                              "1"))))

(defn pandoc-orderedlist->hiccup [[list-attrs items]]
  (->> items
       (map (partial wrap-block [:li]))
       (into [:ol (pandoc-list-attr->hiccup list-attrs)])))

(defn pandoc-bulletlist->hiccup [items]
  (into [:ul] (map (partial wrap-block [:li]) items)))

(defn type [block-or-inline]
  (:t block-or-inline))

(defn children [block-or-inline]
  (:c block-or-inline))

;; See: https://hackage.haskell.org/package/pandoc-types-1.23.1/docs/Text-Pandoc-Definition.html#t:Block
(defn pandoc-block->hiccup [{:keys [t c] :as block}]
  (case (keyword t)
    :Plain (map pandoc-inline->hiccup c)
    :Para (wrap-inline [:p] c)
    :Header (pandoc-header->hiccup c)
    :BlockQuote (wrap-block [:blockquote] c)
    :CodeBlock (pandoc-codeblock->hiccup c)
    :OrderedList (pandoc-orderedlist->hiccup c)
    :BulletList (pandoc-bulletlist->hiccup c)))

(defn pandoc->hiccup [{:keys [blocks]}]
  (map pandoc-block->hiccup blocks))

(defn -main [& _args]
  (let [in (slurp System/in)]
    (prn (pandoc->hiccup (json/parse-string in keyword)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Inline predicate functions
;;

(defn emph? [block-or-inline]
  (= pandocir.type/emph (pandocir.core/type block-or-inline)))

(defn quoted? [block-or-inline]
  (= pandocir.type/quoted (pandocir.core/type block-or-inline)))

(defn small-caps? [block-or-inline]
  (= pandocir.type/small-caps (pandocir.core/type block-or-inline)))

(defn space? [block-or-inline]
  (= pandocir.type/space (pandocir.core/type block-or-inline)))

(defn str? [block-or-inline]
  (= pandocir.type/str (pandocir.core/type block-or-inline)))

(defn strikeout? [block-or-inline]
  (= pandocir.type/strikeout (pandocir.core/type block-or-inline)))

(defn strong? [block-or-inline]
  (= pandocir.type/strong (pandocir.core/type block-or-inline)))

(defn subscript? [block-or-inline]
  (= pandocir.type/subscript (pandocir.core/type block-or-inline)))

(defn superscript? [block-or-inline]
  (= pandocir.type/superscript (pandocir.core/type block-or-inline)))

(defn inline? [block-or-inline]
  (contains? pandocir.type/inline-types (:t block-or-inline)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Block predicate functions
;;

(defn block-quote? [block-or-inline]
  (= pandocir.type/block-quote (pandocir.core/type block-or-inline)))

(defn bullet-list? [block-or-inline]
  (= pandocir.type/bullet-list (pandocir.core/type block-or-inline)))

(defn code-block? [block-or-inline]
  (= pandocir.type/code-block (pandocir.core/type block-or-inline)))

(defn header? [block-or-inline]
  (= pandocir.type/header (pandocir.core/type block-or-inline)))

(defn ordered-list? [block-or-inline]
  (= pandocir.type/ordered-list (pandocir.core/type block-or-inline)))

(defn para? [block-or-inline]
  (= pandocir.type/para (pandocir.core/type block-or-inline)))

(defn plain? [block-or-inline]
  (= pandocir.type/plain (pandocir.core/type block-or-inline)))

(defn block? [block-or-inline]
  (contains? pandocir.type/block-types (pandocir.type/type block-or-inline)))
