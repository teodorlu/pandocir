(ns pandocir.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [pandocir.core :as pandoc]
   [babashka.cli]
   [clojure.java.shell :as sh]
   [cheshire.core :as json]))

;; Utility functions for calling pandoc. Useful for checking tests against pandoc.
(defn call-pandoc [input from to]
  (let [{:keys [out err]} (sh/sh "pandoc" "-f" from "-t" to :in input)]
    (when err (print err))
    out))

(defn block-test [block to]
  (-> {:pandoc-api-version [1 23 1]
       :meta {}
       :blocks [block]}
      (json/encode)
      (call-pandoc "json" to)))

(defn inline-test [inline to]
  (block-test {:t "Plain" :c [inline]}))

;; Inline Element Tests

(deftest space-test
  (is (= " " (pandoc/inline->hiccup {:t "Space"}))))

(deftest string-test
  (is (= "there"
         (pandoc/inline->hiccup
          {:t "Str", :c "there"}))))

(deftest emph-test
  (is (= [:em "there"]
         (pandoc/inline->hiccup
          {:t "Emph", :c [{:t "Str", :c "there"}]}))))

(deftest strong-test
  (is (= [:strong "there"]
         (pandoc/inline->hiccup
          {:t "Strong", :c [{:t "Str", :c "there"}]}))))

(deftest strikeout-test
  (is (= [:del "struckout"]
         (pandoc/inline->hiccup
          {:t "Strikeout", :c [{:t "Str", :c "struckout"}]}))))

(deftest superscript-test
  (is (= [:sup "super"]
         (pandoc/inline->hiccup
          {:t "Superscript", :c [{:t "Str", :c "super"}]}))))

(deftest subscript-test
  (is (= [:sub "sub"]
         (pandoc/inline->hiccup
          {:t "Subscript", :c [{:t "Str", :c "sub"}]}))))

(deftest smallcaps-test
  (is (= [:span {:style {:font-variant "small-caps"}} "smallcaps"]
         (pandoc/inline->hiccup
          {:t "SmallCaps", :c [{:t "Str", :c "smallcaps"}]}))))

(deftest quoted-single-test
  (is (= '("‘" "single quote" "’")
         (pandoc/inline->hiccup
          {:t "Quoted", :c [{:t "SingleQuote"} [{:t "Str", :c "single quote"}]]}))))

(deftest quoted-double-test
  (is (= '("“" "double quote" "”")
         (pandoc/inline->hiccup
          {:t "Quoted", :c [{:t "DoubleQuote"} [{:t "Str", :c "double quote"}]]}))))



;; Block Element Tests

(deftest plain-test
  (is (= '("Plain" " " "text")
         (pandoc/block->hiccup
          {:t "Plain", :c [{:t "Str", :c "Plain"} {:t "Space"} {:t "Str", :c "text"}]}))))

(deftest para-test
  (is (= [:p "hi," " " [:em "there"] "!"]
         (pandoc/block->hiccup
          {:t "Para",
           :c
           [{:t "Str", :c "hi,"}
            {:t "Space"}
            {:t "Emph", :c [{:t "Str", :c "there"}]}
            {:t "Str", :c "!"}]}))))

(deftest header-test-level-1
  (is (= [:h1 "Header text"]
         (pandoc/block->hiccup
          {:t "Header", :c [1 ["",[],[]] [{:t "Str", :c "Header text"}]]}))))

(deftest header-test-level-2-with-attributes
  (is (= [:h2 {:id "sec-1" :class ["title"]} "Subheader text"]
         (pandoc/block->hiccup
          {:t "Header", :c [2 ["sec-1",["title"],[]] [{:t "Str", :c "Subheader text"}]]}))))

(deftest header-test-level-3-nested-inlines
  (is (= [:h3 "Nested" " " [:em "inlines"] " " "here"]
         (pandoc/block->hiccup
          {:t "Header", :c [3 ["",[],[]] [{:t "Str", :c "Nested"} {:t "Space"} {:t "Emph", :c [{:t "Str", :c "inlines"}]} {:t "Space"} {:t "Str", :c "here"}]]}))))

(deftest blockquote-test
  (is (= [:blockquote [:p "Quote text"]]
         (pandoc/block->hiccup
          {:t "BlockQuote", :c [{:t "Para", :c [{:t "Str", :c "Quote text"}]}]}))))

(deftest codeblock-test
  (is (= [:pre [:code "code here"]]
         (pandoc/block->hiccup
          {:t "CodeBlock", :c [["", [], []] "code here"]}))))

(deftest codeblock-test-with-attr
  (is (= [:pre {:id "id", :class ["class1" "class2"], "data-key" "value"}
          [:code "code here"]]
         (pandoc/block->hiccup
          {:t "CodeBlock", :c [["id", ["class1", "class2"], [["key", "value"]]], "code here"]}))))

(deftest orderedlist-test
  (is (= '[:ol {:type "1"} [:li ("Item 1")] [:li ("Item 2")]]
         (pandoc/block->hiccup
          {:t "OrderedList", :c [[1, "Decimal", "Period"] [[{:t "Plain", :c [{:t "Str", :c "Item 1"}]}] [{:t "Plain", :c [{:t "Str", :c "Item 2"}]}]]]}))))

(deftest orderedlist-rich-test
  (is (= '[:ol {:type "i"} [:li ("First Item")] [:li ([:em "Second"] " Item")]]
         (pandoc/block->hiccup
          {:t "OrderedList",
           :c [[1, "LowerRoman", "Period"],
               [[{:t "Plain", :c [{:t "Str", :c "First Item"}]}],
                [{:t "Plain", :c [{:t "Emph", :c [{:t "Str", :c "Second"}]} {:t "Str", :c " Item"}]}]]]}))))

(deftest bulletlist-test
  (is (= '[:ul [:li ("Item 1")] [:li ("Item 2")]]
         (pandoc/block->hiccup
          {:t "BulletList", :c [[{:t "Plain", :c [{:t "Str", :c "Item 1"}]}] [{:t "Plain", :c [{:t "Str", :c "Item 2"}]}]]}))))

(deftest nested-list-test
  (is (= '[:ul [:li ("Item 1") [:ul [:li ("Subitem 1")] [:li ("Subitem 2")]]] [:li ("Item 2")]]
         (pandoc/block->hiccup
          {:t "BulletList",
           :c [[{:t "Plain", :c [{:t "Str", :c "Item 1"}]},
                {:t "BulletList",
                 :c [[{:t "Plain", :c [{:t "Str", :c "Subitem 1"}]}],
                     [{:t "Plain", :c [{:t "Str", :c "Subitem 2"}]}]]}],
               [{:t "Plain", :c [{:t "Str", :c "Item 2"}]}]]}))))

(deftest hiccup-test
  (is (= '([:p "hei"] [:p "oslo" " " "clojure"])
         (pandoc/document->hiccup
          {:pandoc-api-version [1 23 1],
           :meta {},
           :blocks
           [{:t "Para", :c [{:t "Str", :c "hei"}]}
            {:t "Para",
             :c [{:t "Str", :c "oslo"} {:t "Space"} {:t "Str", :c "clojure"}]}]}))))

(deftest definition-list-two-definitions
  (is (= '[:dl
           [:dt "java"]
           [:dd ("island")]
           [:dd ("coffee" " " "beans")]]
         (pandoc/block->hiccup
          {:c [[[{:c "java", :t "Str"}]
                [[{:c [{:c "island", :t "Str"}], :t "Plain"}]
                 [{:c [{:c "coffee", :t "Str"} {:t "Space"} {:t "Str" :c "beans"}], :t "Plain"}]]]],
           :t "DefinitionList"}))))

(deftest definition-list-one-definition
  (is (= '[:dl
           [:dt "Term"]
           [:dd ("Definition")]]
         (pandoc/block->hiccup
          {:c [[[{:c "Term", :t "Str"}]
                [[{:c [{:c "Definition", :t "Str"}]
                   :t "Plain"}]]]],
           :t "DefinitionList"}))))

(deftest lineblock-test
  (is (= [:div {:class ["line-block"]} "Line 1" [:br] "Line 2"]
         (pandoc/block->hiccup
          {:t "LineBlock", :c [[{:t "Str", :c "Line 1"}] [{:t "Str", :c "Line 2"}]]}))))

(deftest lineblock-test-multiple
  (is (= [:div {:class ["line-block"]} "Line 1" [:br] "Line 2" [:br] "Line 3"]
         (pandoc/block->hiccup
          {:t "LineBlock", :c [[{:t "Str", :c "Line 1"}] [{:t "Str", :c "Line 2"}] [{:t "Str", :c "Line 3"}]]}))))

(deftest rawblock-test
  (is (= [:div {:innerHTML "<div>raw HTML</div>"}]
         (pandoc/block->hiccup
          {:t "RawBlock", :c ["html" "<div>raw HTML</div>"]}))))

(deftest rawblock-unnsupported-test
  (is (= nil
         (pandoc/block->hiccup
          {:t "RawBlock", :c ["latex" "$P \to \neg\negP$"]}))))

(deftest horizontalrule-test
  (is (= [:hr]
         (pandoc/block->hiccup
          {:t "HorizontalRule"}))))

(deftest div-test
  (is (= [:div {:class ["container"]} [:p "Content"]]
         (pandoc/block->hiccup
          {:t "Div", :c [["", ["container"], []], [{:t "Para", :c [{:t "Str", :c "Content"}]}]]}))))

(deftest div-no-attr-test
  (is (= [:div [:p "Content"]]
         (pandoc/block->hiccup
          {:t "Div", :c [["", [], []], [{:t "Para", :c [{:t "Str", :c "Content"}]}]]}))))

(comment
  ;; The following tests do not yet pass---but we expect them to pass when we've written more code.

  (deftest table-test
    (is (= '[:table
             [:thead [:tr {:class ["header"]} [:th ("header" " " "1")]]]
             [:tbody [:tr {:class ["odd"]} [:td ("cell" " " "1")]]]]
           (pandoc/block->hiccup
            {:t "Table",
             :c
             [["" [] []]
              [nil []]
              [[{:t "AlignDefault"} {:t "ColWidthDefault"}]]
              [["" [] []]
               [[["" [] []]
                 [[["" [] []] {:t "AlignDefault"}
                   1
                   1
                   [{:t "Plain",
                     :c [{:t "Str", :c "header"} {:t "Space"} {:t "Str", :c "1"}]}]]]]]]
              [[["" [] []]
                0
                []
                [[["" [] []]
                  [[["" [] []]
                    {:t "AlignDefault"}
                    1
                    1
                    [{:t "Plain",
                      :c [{:t "Str", :c "cell"} {:t "Space"} {:t "Str", :c "1"}]}]]]]]]]
              [["" [] []] []]]}))))


  (deftest figure-test
    (is (= [:figure [:figcaption "Caption"] [:p "Content"]]
           (pandoc/block->hiccup
            {:t "Figure", :c [["", [], []], ["Caption", []], [{:t "Para", :c [{:t "Str", :c "Content"}]}]]}))))

  ;; Document Tests

  (deftest document-with-meta-test
    (is (= '([:meta {:title "Document Title"}]
             [:p "Content"])
           (pandoc/document->hiccup
            {:pandoc-api-version [1 23 1],
             :meta {:title {:t "MetaInlines", :c [{:t "Str", :c "Document Title"}]}},
             :blocks [{:t "Para", :c [{:t "Str", :c "Content"}]}]}))))

  (deftest complex-nested-test
    (is (= [:div [:p "Complex" [:strong "nested"] "content"]]
           (pandoc/block->hiccup
            {:t "Div",
             :c [["", [], []],
                 [{:t "Para",
                   :c [{:t "Str", :c "Complex"},
                       {:t "Strong", :c [{:t "Str", :c "nested"}]},
                       {:t "Str", :c "content"}]}]]})))))
