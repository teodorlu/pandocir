(ns pandocir.ir-test
  (:require [pandocir.ir :as sut]
            [clojure.test :as t]))

(def test-data
  [[:pandocir.test/space-test
    {:pandocir.format/hiccup " "
     :pandocir.format/edn {:t "Space"}}]

   [:pandocir.test/string-test
    {:pandocir.format/hiccup "there"
     :pandocir.format/edn {:t "Str", :c "there"}}]

   [:pandocir.test/emph-test
    {:pandocir.format/hiccup [:em "there"]
     :pandocir.format/edn {:t "Emph", :c [{:t "Str", :c "there"}]}}]

   [:pandocir.test/strong-test
    {:pandocir.format/hiccup [:strong "there"]
     :pandocir.format/edn {:t "Strong", :c [{:t "Str", :c "there"}]}}]

   [:pandocir.test/strikeout-test
    {:pandocir.format/hiccup [:del "struckout"]
     :pandocir.format/edn {:t "Strikeout", :c [{:t "Str", :c "struckout"}]}}]

   [:pandocir.test/superscript-test
    {:pandocir.format/hiccup [:sup "super"]
     :pandocir.format/edn {:t "Superscript", :c [{:t "Str", :c "super"}]}}]

   [:pandocir.test/subscript-test
    {:pandocir.format/hiccup [:sub "sub"]
     :pandocir.format/edn {:t "Subscript", :c [{:t "Str", :c "sub"}]}}]

   [:pandocir.test/smallcaps-test
    {:pandocir.format/hiccup [:span {:style {:font-variant "small-caps"}} "smallcaps"]
     :pandocir.format/edn {:t "SmallCaps", :c [{:t "Str", :c "smallcaps"}]}}]

   [:pandocir.test/quoted-single-test
    {:pandocir.format/hiccup '("‘" "single quote" "’")
     :pandocir.format/edn {:t "Quoted", :c [{:t "SingleQuote"} [{:t "Str", :c "single quote"}]]}}]

   [:pandocir.test/quoted-double-test
    {:pandocir.format/hiccup '("“" "double quote" "”")
     :pandocir.format/edn {:t "Quoted", :c [{:t "DoubleQuote"} [{:t "Str", :c "double quote"}]]}}]


   ;; Block Element Tests

   [:pandocir.test/plain-test
    {:pandocir.format/hiccup '("Plain" " " "text")
     :pandocir.format/edn {:t "Plain", :c [{:t "Str", :c "Plain"} {:t "Space"} {:t "Str", :c "text"}]}}]

   [:pandocir.test/para-test
    {:pandocir.format/hiccup [:p "hi," " " [:em "there"] "!"]
     :pandocir.format/edn {:t "Para",
                           :c
                           [{:t "Str", :c "hi,"}
                            {:t "Space"}
                            {:t "Emph", :c [{:t "Str", :c "there"}]}
                            {:t "Str", :c "!"}]}}]

   [:pandocir.test/header-test-level-1
    {:pandocir.format/hiccup [:h1 "Header text"]
     :pandocir.format/edn {:t "Header", :c [1 ["",[],[]] [{:t "Str", :c "Header text"}]]}}]

   [:pandocir.test/header-test-level-2-with-attributes
    {:pandocir.format/hiccup [:h2 {:id "sec-1" :class ["title"]} "Subheader text"]
     :pandocir.format/edn {:t "Header", :c [2 ["sec-1",["title"],[]] [{:t "Str", :c "Subheader text"}]]}}]

   [:pandocir.test/header-test-level-3-nested-inlines
    {:pandocir.format/hiccup [:h3 "Nested" " " [:em "inlines"] " " "here"]
     :pandocir.format/edn {:t "Header", :c [3 ["",[],[]] [{:t "Str", :c "Nested"} {:t "Space"} {:t "Emph", :c [{:t "Str", :c "inlines"}]} {:t "Space"} {:t "Str", :c "here"}]]}}]

   [:pandocir.test/blockquote-test
    {:pandocir.format/hiccup [:blockquote [:p "Quote text"]]
     :pandocir.format/edn {:t "BlockQuote", :c [{:t "Para", :c [{:t "Str", :c "Quote text"}]}]}}]

   [:pandocir.test/codeblock-test
    {:pandocir.format/hiccup [:pre [:code "code here"]]
     :pandocir.format/edn {:t "CodeBlock", :c [["", [], []] "code here"]}}]

   [:pandocir.test/codeblock-test-with-attr
    {:pandocir.format/hiccup [:pre {:id "id", :class ["class1" "class2"], "data-key" "value"}
                              [:code "code here"]]
     :pandocir.format/edn {:t "CodeBlock", :c [["id", ["class1", "class2"], [["key", "value"]]], "code here"]}}]

   [:pandocir.test/orderedlist-test
    {:pandocir.format/hiccup '[:ol {:type "1"} [:li ("Item 1")] [:li ("Item 2")]]
     :pandocir.format/edn {:t "OrderedList", :c [[1, "Decimal", "Period"] [[{:t "Plain", :c [{:t "Str", :c "Item 1"}]}] [{:t "Plain", :c [{:t "Str", :c "Item 2"}]}]]]}}]

   [:pandocir.test/orderedlist-rich-test
    {:pandocir.format/hiccup '[:ol {:type "i"} [:li ("First Item")] [:li ([:em "Second"] " Item")]]
     :pandocir.format/edn {:t "OrderedList",
                           :c [[1, "LowerRoman", "Period"],
                               [[{:t "Plain", :c [{:t "Str", :c "First Item"}]}],
                                [{:t "Plain", :c [{:t "Emph", :c [{:t "Str", :c "Second"}]} {:t "Str", :c " Item"}]}]]]}}]

   [:pandocir.test/bulletlist-test
    {:pandocir.format/hiccup '[:ul [:li ("Item 1")] [:li ("Item 2")]]
     :pandocir.format/edn {:t "BulletList", :c [[{:t "Plain", :c [{:t "Str", :c "Item 1"}]}] [{:t "Plain", :c [{:t "Str", :c "Item 2"}]}]]}}]

   [:pandocir.test/nested-list-test
    {:pandocir.format/hiccup '[:ul [:li ("Item 1") [:ul [:li ("Subitem 1")] [:li ("Subitem 2")]]] [:li ("Item 2")]]
     :pandocir.format/edn {:t "BulletList",
                           :c [[{:t "Plain", :c [{:t "Str", :c "Item 1"}]},
                                {:t "BulletList",
                                 :c [[{:t "Plain", :c [{:t "Str", :c "Subitem 1"}]}],
                                     [{:t "Plain", :c [{:t "Str", :c "Subitem 2"}]}]]}],
                               [{:t "Plain", :c [{:t "Str", :c "Item 2"}]}]]}}]

   [:pandocir.test/hiccup-test
    {:pandocir.format/hiccup '([:p "hei"] [:p "oslo" " " "clojure"])
     :pandocir.format/edn {:pandoc-api-version [1 23 1],
                           :meta {},
                           :blocks
                           [{:t "Para", :c [{:t "Str", :c "hei"}]}
                            {:t "Para",
                             :c [{:t "Str", :c "oslo"} {:t "Space"} {:t "Str", :c "clojure"}]}]}}]

   [:pandocir.test/definition-list-two-definitions
    {:pandocir.format/hiccup '[:dl
                               [:dt "java"]
                               [:dd ("island")]
                               [:dd ("coffee" " " "beans")]]
     :pandocir.format/edn {:c [[[{:c "java", :t "Str"}]
                                [[{:c [{:c "island", :t "Str"}], :t "Plain"}]
                                 [{:c [{:c "coffee", :t "Str"} {:t "Space"} {:t "Str" :c "beans"}], :t "Plain"}]]]],
                           :t "DefinitionList"}}]

   [:pandocir.test/definition-list-one-definition
    {:pandocir.format/hiccup '[:dl
                               [:dt "Term"]
                               [:dd ("Definition")]]
     :pandocir.format/edn {:c [[[{:c "Term", :t "Str"}]
                                [[{:c [{:c "Definition", :t "Str"}]
                                   :t "Plain"}]]]],
                           :t "DefinitionList"}}]

   [:pandocir.test/lineblock-test
    {:pandocir.format/hiccup [:div {:class ["line-block"]} "Line 1" [:br] "Line 2"]
     :pandocir.format/edn {:t "LineBlock", :c [[{:t "Str", :c "Line 1"}] [{:t "Str", :c "Line 2"}]]}}]

   [:pandocir.test/lineblock-test-multiple
    {:pandocir.format/hiccup [:div {:class ["line-block"]} "Line 1" [:br] "Line 2" [:br] "Line 3"]
     :pandocir.format/edn {:t "LineBlock", :c [[{:t "Str", :c "Line 1"}] [{:t "Str", :c "Line 2"}] [{:t "Str", :c "Line 3"}]]}}]

   [:pandocir.test/rawblock-test
    {:pandocir.format/hiccup [:div {:innerHTML "<div>raw HTML</div>"}]
     :pandocir.format/edn {:t "RawBlock", :c ["html" "<div>raw HTML</div>"]}}]

   [:pandocir.test/rawblock-unnsupported-test
    {:pandocir.format/hiccup nil
     :pandocir.format/edn {:t "RawBlock", :c ["latex" "$P \to \neg\negP$"]}}]

   [:pandocir.test/horizontalrule-test
    {:pandocir.format/hiccup [:hr]
     :pandocir.format/edn {:t "HorizontalRule"}}]

   [:pandocir.test/table-test
    {:pandocir.format/hiccup '[:table
                               [:thead [:tr {:class ["header"]} [:th ("header" " " "1")]]]
                               [:tbody [:tr {:class ["odd"]} [:td ("cell" " " "1")]]]]
     :pandocir.format/edn {:t "Table",
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
                            [["" [] []] []]]}}]

   [:pandocir.test/div-test
    {:pandocir.format/hiccup [:div {:class ["container"]} [:p "Content"]]
     :pandocir.format/edn {:t "Div", :c [["", ["container"], []], [{:t "Para", :c [{:t "Str", :c "Content"}]}]]}}]

   [:pandocir.test/div-no-attr-test
    {:pandocir.format/hiccup [:div [:p "Content"]]
     :pandocir.format/edn {:t "Div", :c [["", [], []], [{:t "Para", :c [{:t "Str", :c "Content"}]}]]}}]])


(defn run-on-test-data []
  (doseq [[test-name {:keys [:pandocir.format/edn]}] test-data]
    (prn test-name)
    (clojure.pprint/pprint (sut/pandoc->ir edn))
    (println)))

(comment
  ;; The following tests do not yet pass---but we expect them to pass when we've written more code.
  {[:pandocir.test/table-test
    {:pandocir.format/hiccup '[:table
                               [:thead [:tr {:class ["header"]} [:th ("header" " " "1")]]]
                               [:tbody [:tr {:class ["odd"]} [:td ("cell" " " "1")]]]]
     :pandocir.format/edn {:t "Table",
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
                            [["" [] []] []]]}}]


   [:pandocir.test/figure-test
    {:pandocir.format/hiccup [:figure [:figcaption "Caption"] [:p "Content"]]
     :pandocir.format/edn {:t "Figure", :c [["", [], []], ["Caption", []], [{:t "Para", :c [{:t "Str", :c "Content"}]}]]}}]

   ;; Document Tests

   [:pandocir.test/document-with-meta-test
    {:pandocir.format/hiccup '([:meta {:title "Document Title"}]
                               [:p "Content"])
     :pandocir.format/edn {:pandoc-api-version [1 23 1],
                           :meta {:title {:t "MetaInlines", :c [{:t "Str", :c "Document Title"}]}},
                           :blocks [{:t "Para", :c [{:t "Str", :c "Content"}]}]}}]

   [:pandocir.test/complex-nested-test
    {:pandocir.format/hiccup [:div [:p "Complex" [:strong "nested"] "content"]]
     :pandocir.format/edn {:t "Div",
                           :c [["", [], []],
                               [{:t "Para",
                                 :c [{:t "Str", :c "Complex"},
                                     {:t "Strong", :c [{:t "Str", :c "nested"}]},
                                     {:t "Str", :c "content"}]}]]}}]})
