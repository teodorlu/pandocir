(ns pandocir.ir-test
  (:require [pandocir.ir :as ir]
            [clojure.test :refer [deftest testing is]]))

(def cases
  [{:description "singlequote",
    :pandoc {:t "SingleQuote"},
    :ir {:pandocir/type :pandocir.type/single-quote}}

   {:description "doublequote",
    :pandoc {:t "DoubleQuote"},
    :ir {:pandocir/type :pandocir.type/double-quote}}

   {:description "authorintext",
    :pandoc {:t "AuthorInText"},
    :ir {:pandocir/type :pandocir.type/author-in-text}}

   {:description "suppressauthor",
    :pandoc {:t "SuppressAuthor"},
    :ir {:pandocir/type :pandocir.type/suppress-author}}

   {:description "normalcitation",
    :pandoc {:t "NormalCitation"},
    :ir {:pandocir/type :pandocir.type/normal-citation}}

   {:description "citation",
    :pandoc
    {:citationId "jameson:unconscious",
     :citationPrefix [{:t "Str", :c "cf"}],
     :citationSuffix [{:t "Space"} {:t "Str", :c "123"}],
     :citationMode {:t "NormalCitation"},
     :citationNoteNum 0,
     :citationHash 0},
    :ir
    {:pandocir.citation/id "jameson:unconscious",
     :pandocir.citation/prefix
     [{:pandocir/type :pandocir.type/str, :pandocir/text "cf"}],
     :pandocir.citation/suffix
     [{:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "123"}],
     :pandocir.citation/mode
     {:pandocir/type :pandocir.type/normal-citation},
     :pandocir.citation/note-num 0,
     :pandocir.citation/hash 0}}

   {:description "str",
    :pandoc {:t "Str", :c "Hello"},
    :ir {:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}}

   {:description "emph",
    :pandoc {:t "Emph", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/emph,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "underline",
    :pandoc {:t "Underline", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/underline,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "strong",
    :pandoc {:t "Strong", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/strong,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "strikeout",
    :pandoc {:t "Strikeout", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/strikeout,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "superscript",
    :pandoc {:t "Superscript", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/superscript,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "subscript",
    :pandoc {:t "Subscript", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/subscript,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "smallcaps",
    :pandoc {:t "SmallCaps", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/small-caps,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "quoted",
    :pandoc {:t "Quoted", :c [{:t "SingleQuote"} [{:t "Str", :c "Hello"}]]},
    :ir
    {:pandocir/type :pandocir.type/quoted,
     :pandocir.quote/type :pandocir.type/single-quote,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "cite",
    :pandoc
    {:t "Cite",
     :c
     [[{:citationId "jameson:unconscious",
        :citationPrefix [{:t "Str", :c "cf"}],
        :citationSuffix [{:t "Space"} {:t "Str", :c "12"}],
        :citationMode {:t "NormalCitation"},
        :citationNoteNum 0,
        :citationHash 0}]
      [{:t "Str", :c "[cf"}
       {:t "Space"}
       {:t "Str", :c "@jameson:unconscious"}
       {:t "Space"}
       {:t "Str", :c "12]"}]]},
    :ir
    {:pandocir/type :pandocir.type/cite,
     :pandocir/citations
     [{:pandocir.citation/id "jameson:unconscious",
       :pandocir.citation/prefix
       [{:pandocir/type :pandocir.type/str, :pandocir/text "cf"}],
       :pandocir.citation/suffix
       [{:pandocir/type :pandocir.type/space}
        {:pandocir/type :pandocir.type/str, :pandocir/text "12"}],
       :pandocir.citation/mode
       {:pandocir/type :pandocir.type/normal-citation},
       :pandocir.citation/note-num 0,
       :pandocir.citation/hash 0}],
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "[cf"}
      {:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "@jameson:unconscious"}
      {:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "12]"}]}}

   {:description "code",
    :pandoc {:t "Code", :c [["" [] [["language" "haskell"]]] "foo bar"]},
    :ir
    {:pandocir/type :pandocir.type/code,
     :pandocir/text "foo bar",
     :pandocir.attr/id "",
     :pandocir.attr/classes [],
     :pandocir.attr/keyvals [["language" "haskell"]]}}

   {:description "space",
    :pandoc {:t "Space"},
    :ir {:pandocir/type :pandocir.type/space}}

   {:description "softbreak",
    :pandoc {:t "SoftBreak"},
    :ir {:pandocir/type :pandocir.type/soft-break}}

   {:description "linebreak",
    :pandoc {:t "LineBreak"},
    :ir {:pandocir/type :pandocir.type/line-break}}

   {:description "rawinline",
    :pandoc {:t "RawInline", :c ["html" "<div>foo bar</div>"]},
    :ir
    {:pandocir/type :pandocir.type/raw-inline,
     :pandocir/format "html",
     :pandocir/text "<div>foo bar</div>"}}

   {:description "link",
    :pandoc
    {:t "Link",
     :c
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      [{:t "Str", :c "a"}
       {:t "Space"}
       {:t "Str", :c "famous"}
       {:t "Space"}
       {:t "Str", :c "site"}]
      ["https://www.google.com" "google"]]},
    :ir
    {:pandocir/type :pandocir.type/link,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "a"}
      {:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "famous"}
      {:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "site"}],
     :pandocir.attr/id "id",
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]],
     :pandocir.link/href "https://www.google.com",
     :pandocir.link/title "google"}}

   {:description "image",
    :pandoc
    {:t "Image",
     :c
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      [{:t "Str", :c "a"}
       {:t "Space"}
       {:t "Str", :c "famous"}
       {:t "Space"}
       {:t "Str", :c "image"}]
      ["my_img.png" "image"]]},
    :ir
    {:pandocir/type :pandocir.type/image,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "a"}
      {:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "famous"}
      {:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "image"}],
     :pandocir.attr/id "id",
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]],
     :pandocir.image/src "my_img.png",
     :pandocir.image/title "image"}}

   {:description "note",
    :pandoc {:t "Note", :c [{:t "Para", :c [{:t "Str", :c "Hello"}]}]},
    :ir
    {:pandocir/type :pandocir.type/note,
     :pandocir/blocks
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}]}}

   {:description "span",
    :pandoc
    {:t "Span",
     :c [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] [{:t "Str", :c "Hello"}]]},
    :ir
    {:pandocir/type :pandocir.type/span,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}],
     :pandocir.attr/id "id",
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]]}}

   {:description "plain",
    :pandoc {:t "Plain", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/plain,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "para",
    :pandoc {:t "Para", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/para,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:description "line-block",
    :pandoc
    {:t "LineBlock", :c [[{:t "Str", :c "Hello"}] [{:t "Str", :c "Moin"}]]},
    :ir
    {:pandocir/type :pandocir.type/line-block,
     :pandocir/inlines
     [[{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]
      [{:pandocir/type :pandocir.type/str, :pandocir/text "Moin"}]]}}

   {:description "code-block",
    :pandoc
    {:t "CodeBlock", :c [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] "Foo Bar"]},
    :ir
    {:pandocir/type :pandocir.type/code-block,
     :pandocir/text "Foo Bar",
     :pandocir.attr/id "id",
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]]}}

   {:description "raw-block",
    :pandoc {:t "RawBlock", :c ["html" "<div>foo bar</div>"]},
    :ir
    {:pandocir/type :pandocir.type/raw-block,
     :pandocir/format "html",
     :pandocir/text "<div>foo bar</div>"}}

   {:description "block-quote",
    :pandoc {:t "BlockQuote", :c [{:t "Para", :c [{:t "Str", :c "Hello"}]}]},
    :ir
    {:pandocir/type :pandocir.type/block-quote,
     :pandocir/blocks
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}]}}

   {:description "ordered-list",
    :pandoc
    {:t "OrderedList",
     :c
     [[1 {:t "Decimal"} {:t "Period"}]
      [[{:t "Para", :c [{:t "Str", :c "foo"}]}]
       [{:t "Para", :c [{:t "Str", :c "bar"}]}]]]},
    :ir
    {:pandocir/type :pandocir.type/ordered-list,
     :pandocir/list-items
     [[{:pandocir/type :pandocir.type/para,
        :pandocir/inlines
        [{:pandocir/type :pandocir.type/str, :pandocir/text "foo"}]}]
      [{:pandocir/type :pandocir.type/para,
        :pandocir/inlines
        [{:pandocir/type :pandocir.type/str, :pandocir/text "bar"}]}]],
     :pandocir.list-attr/start 1,
     :pandocir.list-attr/style :pandocir.type/decimal,
     :pandocir.list-attr/delim :pandocir.type/period}}

   {:description "bullet-list",
    :pandoc
    {:t "BulletList",
     :c
     [[{:t "Para", :c [{:t "Str", :c "foo"}]}]
      [{:t "Para", :c [{:t "Str", :c "bar"}]}]]},
    :ir
    {:pandocir/type :pandocir.type/bullet-list,
     :pandocir/list-items
     [[{:pandocir/type :pandocir.type/para,
        :pandocir/inlines
        [{:pandocir/type :pandocir.type/str, :pandocir/text "foo"}]}]
      [{:pandocir/type :pandocir.type/para,
        :pandocir/inlines
        [{:pandocir/type :pandocir.type/str, :pandocir/text "bar"}]}]]}}

   {:description "definition-list",
    :pandoc
    {:t "DefinitionList",
     :c
     [[[{:t "Str", :c "foo"}] [[{:t "Para", :c [{:t "Str", :c "bar"}]}]]]
      [[{:t "Str", :c "fizz"}] [[{:t "Para", :c [{:t "Str", :c "pop"}]}]]]]},
    :ir
    {:pandocir/type :pandocir.type/definition-list,
     :pandocir/definitions
     [[[{:pandocir/type :pandocir.type/str, :pandocir/text "foo"}]
       [[{:pandocir/type :pandocir.type/para,
          :pandocir/inlines
          [{:pandocir/type :pandocir.type/str, :pandocir/text "bar"}]}]]]
      [[{:pandocir/type :pandocir.type/str, :pandocir/text "fizz"}]
       [[{:pandocir/type :pandocir.type/para,
          :pandocir/inlines
          [{:pandocir/type :pandocir.type/str, :pandocir/text "pop"}]}]]]]}}

   {:description "header",
    :pandoc
    {:t "Header",
     :c [2 ["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] [{:t "Str", :c "Head"}]]},
    :ir
    {:pandocir/type :pandocir.type/header,
     :pandocir/level 2,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Head"}],
     :pandocir.attr/id "id",
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]]}}

   {:description "horizontal-rule",
    :pandoc {:t "HorizontalRule"},
    :ir {:pandocir/type :pandocir.type/horizontal-rule}}

   {:description "figure",
    :pandoc
    {:t "Figure",
     :c
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      [[{:t "Str", :c "hello"}]
       [{:t "Para", :c [{:t "Str", :c "cap content"}]}]]
      [{:t "Para", :c [{:t "Str", :c "fig content"}]}]]},
    :ir
    {:pandocir/type :pandocir.type/figure,
     :pandocir/blocks
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "fig content"}]}],
     :pandocir.attr/id "id",
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]],
     :pandocir.caption/short
     [{:pandocir/type :pandocir.type/str, :pandocir/text "hello"}],
     :pandocir.caption/blocks
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "cap content"}]}]}}

   {:description "div",
    :pandoc
    {:t "Div",
     :c
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      [{:t "Para", :c [{:t "Str", :c "Hello"}]}]]},
    :ir
    {:pandocir/type :pandocir.type/div,
     :pandocir/blocks
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}],
     :pandocir.attr/id "id",
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]]}}])

(deftest test-cases
  (doseq [{:keys [description pandoc ir]} cases]
    (testing description
      (is (= (ir/pandoc->ir pandoc) ir))
      (is (= (ir/ir->pandoc ir) pandoc))

      (testing "bijective"
        (is (= pandoc (ir/ir->pandoc (ir/pandoc->ir pandoc)))))

      (testing "idempotent"
        (is (= (ir/pandoc->ir pandoc)
               (ir/pandoc->ir (ir/pandoc->ir pandoc))))))))
