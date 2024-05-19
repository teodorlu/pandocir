(ns teodorlu.pandoc2hiccup
  (:require [cheshire.core :as json]))

(declare pandoc-inline->hiccup)
(declare pandoc-block->hiccup)

;; See: https://hackage.haskell.org/package/pandoc-types-1.23.1/docs/Text-Pandoc-Definition.html#t:Attr
(defn pandoc-attr->hiccup [[id classes keyvals]]
  (cond-> (into {} keyvals)
    (not (empty? id)) (assoc :id id)
    (not (empty? classes)) (assoc :class classes)))


(defn wrap-inline [wrapper content]
  (into wrapper (map pandoc-inline->hiccup content)))

;; See: https://hackage.haskell.org/package/pandoc-types-1.23.1/docs/Text-Pandoc-Definition.html#t:Inline
(defn pandoc-inline->hiccup [{:keys [t c]}]
  (case (keyword t)
    :Space " "
    :Str c
    :Emph (wrap-inline [:em] c)
    :Strong (wrap-inline [:strong] c)
    :Strikeout (wrap-inline [:s] c)
    :Superscript (wrap-inline [:sup] c)
    :Subscript (wrap-inline [:sub] c)
    ;; Pandoc defaults to generating a span with the `smallcaps` class; this
    ;; however assumes the pandoc default css file to be included. I'm opting to
    ;; inline this instead.
    ;; See: https://pandoc.org/chunkedhtml-demo/8.12-inline-formatting.html#small-caps
    :SmallCaps (wrap-inline [:span {:style {:font-variant "small-caps"}}] c)))

(defn pandoc-para->hiccup [c]
  (wrap-inline [:p] c))

(defn pandoc-header->hiccup [[level attr inlines]]
  (let [attributes (pandoc-attr->hiccup attr)]
    (cond-> [(keyword (str "h" level))]
      (not (empty? attributes)) (conj attributes)
      true (into (map pandoc-inline->hiccup inlines)))))

(defn pandoc-blockquote->hiccup [c]
  (into [:blockquote] (map pandoc-block->hiccup c)))

(defn pandoc-codeblock->hiccup [[attr code]]
  (let [attributes (-> attr
                       (update 2 (partial map (fn [[k v]] [(str "data-" k) v])))
                       (pandoc-attr->hiccup))]
    (cond-> [:pre]
      (not (empty? attributes)) (conj attributes)
      true (conj [:code code]))))

;; See: https://hackage.haskell.org/package/pandoc-types-1.23.1/docs/Text-Pandoc-Definition.html#t:Block
(defn pandoc-block->hiccup [{:keys [t c] :as block}]
  (case (keyword t)
    :Para (pandoc-para->hiccup c)
    :Header (pandoc-header->hiccup c)
    :BlockQuote (pandoc-blockquote->hiccup c)
    :CodeBlock (pandoc-codeblock->hiccup c)))

(defn pandoc->hiccup [{:keys [blocks]}]
  (map pandoc-block->hiccup blocks))

(defn -main [& _args]
  (let [in (slurp System/in)]
    (prn (pandoc->hiccup (json/parse-string in keyword)))))
