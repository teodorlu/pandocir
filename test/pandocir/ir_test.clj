(ns pandocir.ir-test
  (:require [pandocir.ir :as ir]
            [clojure.test :refer [deftest testing is]]))

(def cases
  [{:name :pandocir.test/meta,
    :pandoc {:foo {:t "MetaBool", :c true}},
    :ir {:foo {:t "MetaBool", :c true}}}

   {:name :pandocir.test/metamap,
    :pandoc {:t "MetaMap", :c {:foo {:t "MetaBool", :c true}}},
    :ir {:t "MetaMap", :c {:foo {:t "MetaBool", :c true}}}}

   {:name :pandocir.test/metalist,
    :pandoc
    {:t "MetaList", :c [{:t "MetaBool", :c true} {:t "MetaString", :c "baz"}]},
    :ir
    {:t "MetaList", :c [{:t "MetaBool", :c true} {:t "MetaString", :c "baz"}]}}

   {:name :pandocir.test/metabool,
    :pandoc {:t "MetaBool", :c false},
    :ir {:t "MetaBool", :c false}}

   {:name :pandocir.test/metastring,
    :pandoc {:t "MetaString", :c "Hello"},
    :ir {:t "MetaString", :c "Hello"}}

   {:name :pandocir.test/metainlines,
    :pandoc {:t "MetaInlines", :c [{:t "Space"} {:t "SoftBreak"}]},
    :ir
    {:t "MetaInlines",
     :c
     [{:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/soft-break}]}}

   {:name :pandocir.test/metablocks,
    :pandoc {:t "MetaBlocks", :c [{:t "HorizontalRule"} {:t "HorizontalRule"}]},
    :ir
    {:t "MetaBlocks",
     :c
     [{:pandocir/type :pandocir.type/horizontal-rule}
      {:pandocir/type :pandocir.type/horizontal-rule}]}}

   {:name :pandocir.test/singlequote,
    :pandoc {:t "SingleQuote"},
    :ir {:pandocir/type :pandocir.type/single-quote}}

   {:name :pandocir.test/doublequote,
    :pandoc {:t "DoubleQuote"},
    :ir {:pandocir/type :pandocir.type/double-quote}}

   {:name :pandocir.test/authorintext,
    :pandoc {:t "AuthorInText"},
    :ir {:pandocir/type :pandocir.type/author-in-text}}

   {:name :pandocir.test/suppressauthor,
    :pandoc {:t "SuppressAuthor"},
    :ir {:pandocir/type :pandocir.type/suppress-author}}

   {:name :pandocir.test/normalcitation,
    :pandoc {:t "NormalCitation"},
    :ir {:pandocir/type :pandocir.type/normal-citation}}

   {:name :pandocir.test/citation,
    :pandoc
    {:citationId "jameson:unconscious",
     :citationPrefix [{:t "Str", :c "cf"}],
     :citationSuffix [{:t "Space"} {:t "Str", :c "123"}],
     :citationMode {:t "NormalCitation"},
     :citationNoteNum 0,
     :citationHash 0},
    :ir
    {:pandocir.citation/citation-id "jameson:unconscious",
     :pandocir.citation/citation-prefix
     [{:pandocir/type :pandocir.type/str, :pandocir/text "cf"}],
     :pandocir.citation/citation-suffix
     [{:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "123"}],
     :pandocir.citation/citation-mode
     {:pandocir/type :pandocir.type/normal-citation},
     :pandocir.citation/citation-note-num 0,
     :pandocir.citation/citation-hash 0}}

   {:name :pandocir.test/displaymath,
    :pandoc {:t "DisplayMath"},
    :ir {:t "DisplayMath"}}

   {:name :pandocir.test/inlinemath,
    :pandoc {:t "InlineMath"},
    :ir {:t "InlineMath"}}

   {:name :pandocir.test/str,
    :pandoc {:t "Str", :c "Hello"},
    :ir {:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}}

   {:name :pandocir.test/emph,
    :pandoc {:t "Emph", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/emph,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/underline,
    :pandoc {:t "Underline", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/underline,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/strong,
    :pandoc {:t "Strong", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/strong,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/strikeout,
    :pandoc {:t "Strikeout", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/strikeout,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/superscript,
    :pandoc {:t "Superscript", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/superscript,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/subscript,
    :pandoc {:t "Subscript", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/subscript,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/smallcaps,
    :pandoc {:t "SmallCaps", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/small-caps,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/quoted,
    :pandoc {:t "Quoted", :c [{:t "SingleQuote"} [{:t "Str", :c "Hello"}]]},
    :ir
    {:pandocir/type :pandocir.type/quoted,
     :pandocir.quote/type :pandocir.type/single-quote,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/cite,
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
     [{:pandocir.citation/citation-id "jameson:unconscious",
       :pandocir.citation/citation-prefix
       [{:pandocir/type :pandocir.type/str, :pandocir/text "cf"}],
       :pandocir.citation/citation-suffix
       [{:pandocir/type :pandocir.type/space}
        {:pandocir/type :pandocir.type/str, :pandocir/text "12"}],
       :pandocir.citation/citation-mode
       {:pandocir/type :pandocir.type/normal-citation},
       :pandocir.citation/citation-note-num 0,
       :pandocir.citation/citation-hash 0}],
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "[cf"}
      {:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "@jameson:unconscious"}
      {:pandocir/type :pandocir.type/space}
      {:pandocir/type :pandocir.type/str, :pandocir/text "12]"}]}}

   {:name :pandocir.test/code,
    :pandoc {:t "Code", :c [["" [] [["language" "haskell"]]] "foo bar"]},
    :ir
    {:pandocir/type :pandocir.type/code,
     :pandocir/text "foo bar",
     :pandocir.attr/id "",
     :pandocir.attr/classes [],
     :pandocir.attr/keyvals [["language" "haskell"]]}}

   {:name :pandocir.test/space,
    :pandoc {:t "Space"},
    :ir {:pandocir/type :pandocir.type/space}}

   {:name :pandocir.test/softbreak,
    :pandoc {:t "SoftBreak"},
    :ir {:pandocir/type :pandocir.type/soft-break}}

   {:name :pandocir.test/linebreak,
    :pandoc {:t "LineBreak"},
    :ir {:pandocir/type :pandocir.type/line-break}}

   {:name :pandocir.test/rawinline,
    :pandoc {:t "RawInline", :c ["html" "<div>foo bar</div>"]},
    :ir
    {:pandocir/type :pandocir.type/raw-inline,
     :pandocir/format "html",
     :pandocir/text "<div>foo bar</div>"}}

   {:name :pandocir.test/link,
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

   {:name :pandocir.test/image,
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

   {:name :pandocir.test/note,
    :pandoc {:t "Note", :c [{:t "Para", :c [{:t "Str", :c "Hello"}]}]},
    :ir
    {:pandocir/type :pandocir.type/note,
     :pandocir/blocks
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}]}}

   {:name :pandocir.test/span,
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

   {:name :pandocir.test/plain,
    :pandoc {:t "Plain", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/plain,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/para,
    :pandoc {:t "Para", :c [{:t "Str", :c "Hello"}]},
    :ir
    {:pandocir/type :pandocir.type/para,
     :pandocir/inlines
     [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}}

   {:name :pandocir.test/line-block,
    :pandoc
    {:t "LineBlock", :c [[{:t "Str", :c "Hello"}] [{:t "Str", :c "Moin"}]]},
    :ir
    {:pandocir/type :pandocir.type/line-block,
     :pandocir/inlines
     [[{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]
      [{:pandocir/type :pandocir.type/str, :pandocir/text "Moin"}]]}}

   {:name :pandocir.test/code-block,
    :pandoc
    {:t "CodeBlock", :c [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] "Foo Bar"]},
    :ir
    {:pandocir/type :pandocir.type/code-block,
     :pandocir/text "Foo Bar",
     :pandocir.attr/id "id",
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]]}}

   {:name :pandocir.test/raw-block,
    :pandoc {:t "RawBlock", :c ["html" "<div>foo bar</div>"]},
    :ir
    {:pandocir/type :pandocir.type/raw-block,
     :pandocir/format "html",
     :pandocir/text "<div>foo bar</div>"}}

   {:name :pandocir.test/block-quote,
    :pandoc {:t "BlockQuote", :c [{:t "Para", :c [{:t "Str", :c "Hello"}]}]},
    :ir
    {:pandocir/type :pandocir.type/block-quote,
     :pandocir/blocks
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello"}]}]}}

   {:name :pandocir.test/ordered-list,
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

   {:name :pandocir.test/bullet-list,
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

   {:name :pandocir.test/definition-list,
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

   {:name :pandocir.test/header,
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

   {:name :pandocir.test/horizontal-rule,
    :pandoc {:t "HorizontalRule"},
    :ir {:pandocir/type :pandocir.type/horizontal-rule}}

   {:name :pandocir.test/cell,
    :pandoc
    [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
     {:t "AlignLeft"}
     1
     1
     [{:t "Para", :c [{:t "Str", :c "bar"}]}]],
    :ir
    [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
     {:t "AlignLeft"}
     1
     1
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "bar"}]}]]}

   {:name :pandocir.test/table,
    :pandoc
    {:t "Table",
     :c
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      [[{:t "Str", :c "short"}]
       [{:t "Para",
         :c
         [{:t "Str", :c "Demonstration"}
          {:t "Space"}
          {:t "Str", :c "of"}
          {:t "Space"}
          {:t "Str", :c "simple"}
          {:t "Space"}
          {:t "Str", :c "table"}
          {:t "Space"}
          {:t "Str", :c "syntax."}]}]]
      [[{:t "AlignDefault"} {:t "ColWidthDefault"}]
       [{:t "AlignRight"} {:t "ColWidthDefault"}]
       [{:t "AlignLeft"} {:t "ColWidthDefault"}]
       [{:t "AlignCenter"} {:t "ColWidthDefault"}]
       [{:t "AlignDefault"} {:t "ColWidthDefault"}]]
      [["idh" ["klsh"] [["k1h" "v1h"] ["k2h" "v2h"]]]
       [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
         [[["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "Head"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "Right"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "Left"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "Center"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "Default"}]}]]]]]]
      [[["idb" ["klsb"] [["k1b" "v1b"] ["k2b" "v2b"]]]
        1
        [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
          [[["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "ihead12"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "i12"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "i12"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "i12"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "i12"}]}]]]]]
        [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
          [[["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "head12"}]}]]
           [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "12"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "12"}]}]]
           [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "12"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "12"}]}]]]]
         [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
          [[["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "head123"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "123"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "123"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "123"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "123"}]}]]]]
         [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
          [[["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "head1"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "1"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "1"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "1"}]}]]
           [["a" ["b"] [["c" "d"] ["e" "f"]]]
            {:t "AlignDefault"}
            1
            1
            [{:t "Plain", :c [{:t "Str", :c "1"}]}]]]]]]]
      [["idf" ["klsf"] [["k1f" "v1f"] ["k2f" "v2f"]]]
       [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
         [[["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "foot"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "footright"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "footleft"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "footcenter"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:t "Plain", :c [{:t "Str", :c "footdefault"}]}]]]]]]]},
    :ir
    {:pandocir.caption/short
     [{:pandocir/type :pandocir.type/str, :pandocir/text "short"}],
     :pandocir.table/head
     [["idh" ["klsh"] [["k1h" "v1h"] ["k2h" "v2h"]]]
      [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
        [[["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str, :pandocir/text "Head"}]}]]
         [["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str, :pandocir/text "Right"}]}]]
         [["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str, :pandocir/text "Left"}]}]]
         [["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str, :pandocir/text "Center"}]}]]
         [["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str,
              :pandocir/text "Default"}]}]]]]]],
     :pandocir/type :pandocir.type/table,
     :pandocir.attr/keyvals [["k1" "v1"] ["k2" "v2"]],
     :pandocir.caption/blocks
     [{:pandocir/type :pandocir.type/para,
       :pandocir/inlines
       [{:pandocir/type :pandocir.type/str, :pandocir/text "Demonstration"}
        {:pandocir/type :pandocir.type/space}
        {:pandocir/type :pandocir.type/str, :pandocir/text "of"}
        {:pandocir/type :pandocir.type/space}
        {:pandocir/type :pandocir.type/str, :pandocir/text "simple"}
        {:pandocir/type :pandocir.type/space}
        {:pandocir/type :pandocir.type/str, :pandocir/text "table"}
        {:pandocir/type :pandocir.type/space}
        {:pandocir/type :pandocir.type/str, :pandocir/text "syntax."}]}],
     :pandocir.table/foot
     [["idf" ["klsf"] [["k1f" "v1f"] ["k2f" "v2f"]]]
      [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
        [[["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str, :pandocir/text "foot"}]}]]
         [["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str, :pandocir/text "footright"}]}]]
         [["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str, :pandocir/text "footleft"}]}]]
         [["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str,
              :pandocir/text "footcenter"}]}]]
         [["a" ["b"] [["c" "d"] ["e" "f"]]]
          {:t "AlignDefault"}
          1
          1
          [{:pandocir/type :pandocir.type/plain,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str,
              :pandocir/text "footdefault"}]}]]]]]],
     :pandocir.table/body
     [[["idb" ["klsb"] [["k1b" "v1b"] ["k2b" "v2b"]]]
       1
       [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
         [[["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "ihead12"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "i12"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "i12"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "i12"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "i12"}]}]]]]]
       [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
         [[["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "head12"}]}]]
          [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "12"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "12"}]}]]
          [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "12"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "12"}]}]]]]
        [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
         [[["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "head123"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "123"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "123"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "123"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "123"}]}]]]]
        [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
         [[["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "head1"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "1"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "1"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "1"}]}]]
          [["a" ["b"] [["c" "d"] ["e" "f"]]]
           {:t "AlignDefault"}
           1
           1
           [{:pandocir/type :pandocir.type/plain,
             :pandocir/inlines
             [{:pandocir/type :pandocir.type/str, :pandocir/text "1"}]}]]]]]]],
     :pandocir.attr/classes ["kls"],
     :pandocir.attr/id "id",
     :pandocir.table/col-specs
     [[{:t "AlignDefault"} {:t "ColWidthDefault"}]
      [{:t "AlignRight"} {:t "ColWidthDefault"}]
      [{:t "AlignLeft"} {:t "ColWidthDefault"}]
      [{:t "AlignCenter"} {:t "ColWidthDefault"}]
      [{:t "AlignDefault"} {:t "ColWidthDefault"}]]}}

   {:name :pandocir.test/figure,
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

   {:name :pandocir.test/div,
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
  (doseq [{:keys [name pandoc ir]} cases]
    (testing (str name)
      (is (= (ir/pandoc->ir pandoc) ir))
      (is (= (ir/ir->pandoc ir) pandoc))

      (testing "bijective"
        (is (= pandoc (ir/ir->pandoc (ir/pandoc->ir pandoc)))))

      (testing "idempotent"
        (is (= (ir/pandoc->ir pandoc)
               (ir/pandoc->ir (ir/pandoc->ir pandoc))))))))
