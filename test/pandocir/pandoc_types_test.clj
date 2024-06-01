(ns pandocir.pandoc-types-test
  (:require  [clojure.test :as t]))

(def pandoc-types-test
  '[[:pandocir.test/meta
     {:foo {:t "MetaBool", :c true}}]

    [:pandocir.test/metamap
     {:t "MetaMap", :c {:foo {:t "MetaBool", :c true}}}]

    [:pandocir.test/metalist
     {:t "MetaList", :c [{:t "MetaBool", :c true} {:t "MetaString", :c "baz"}]}]

    [:pandocir.test/metabool
     {:t "MetaBool", :c false}]
    [:pandocir.test/metastring
     {:t "MetaString", :c "Hello"}]
    [:pandocir.test/metainlines
     {:t "MetaInlines", :c [{:t "Space"} {:t "SoftBreak"}]}]

    [:pandocir.test/metablocks
     {:t "MetaBlocks", :c [{:t "HorizontalRule"} {:t "HorizontalRule"}]}]
    [:pandocir.test/singlequote
     {:t "SingleQuote"}]
    [:pandocir.test/doublequote
     {:t "DoubleQuote"}]
    [:pandocir.test/authorintext
     {:t "AuthorInText"}]
    [:pandocir.test/suppressauthor
     {:t "SuppressAuthor"}]
    [:pandocir.test/normalcitation
     {:t "NormalCitation"}]
    [:pandocir.test/citation
     {:citationId "jameson:unconscious",
      :citationPrefix [{:t "Str", :c "cf"}],
      :citationSuffix [{:t "Space"} {:t "Str", :c "123"}],
      :citationMode {:t "NormalCitation"},
      :citationNoteNum 0,
      :citationHash 0}]

    [:pandocir.test/displaymath
     {:t "DisplayMath"}]
    [:pandocir.test/inlinemath
     {:t "InlineMath"}]

    [:pandocir.test/str
     {:t "Str", :c "Hello"}]

    [:pandocir.test/emph
     {:t "Emph", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/underline
     {:t "Underline", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/strong
     {:t "Strong", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/strikeout
     {:t "Strikeout", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/superscript
     {:t "Superscript", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/subscript
     {:t "Subscript", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/smallcaps
     {:t "SmallCaps", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/quoted
     {:t "Quoted", :c [{:t "SingleQuote"} [{:t "Str", :c "Hello"}]]}]

    [:pandocir.test/cite
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
        {:t "Str", :c "12]"}]]}]

    [:pandocir.test/code
     {:t "Code", :c [["" [] [["language" "haskell"]]] "foo bar"]}]

    [:pandocir.test/space
     {:t "Space"}]
    [:pandocir.test/softbreak
     {:t "SoftBreak"}]
    [:pandocir.test/linebreak
     {:t "LineBreak"}]
    [:pandocir.test/rawinline
     {:t "RawInline", :c ["tex" "\\foo{bar}"]}]

    [:pandocir.test/link
     {:t "Link",
      :c
      [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
       [{:t "Str", :c "a"}
        {:t "Space"}
        {:t "Str", :c "famous"}
        {:t "Space"}
        {:t "Str", :c "site"}]
       ["https://www.google.com" "google"]]}]

    [:pandocir.test/image
     {:t "Image",
      :c
      [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
       [{:t "Str", :c "a"}
        {:t "Space"}
        {:t "Str", :c "famous"}
        {:t "Space"}
        {:t "Str", :c "image"}]
       ["my_img.png" "image"]]}]

    [:pandocir.test/note
     {:t "Note", :c [{:t "Para", :c [{:t "Str", :c "Hello"}]}]}]

    [:pandocir.test/span
     {:t "Span",
      :c [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] [{:t "Str", :c "Hello"}]]}]

    [:pandocir.test/plain
     {:t "Plain", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/para
     {:t "Para", :c [{:t "Str", :c "Hello"}]}]

    [:pandocir.test/lineblock
     {:t "LineBlock", :c [[{:t "Str", :c "Hello"}] [{:t "Str", :c "Moin"}]]}]

    [:pandocir.test/codeblock
     {:t "CodeBlock", :c [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] "Foo Bar"]}]

    [:pandocir.test/rawblock
     {:t "RawBlock", :c ["tex" "\\foo{bar}"]}]

    [:pandocir.test/blockquote
     {:t "BlockQuote", :c [{:t "Para", :c [{:t "Str", :c "Hello"}]}]}]

    [:pandocir.test/orderedlist
     {:t "OrderedList",
      :c
      [[1 {:t "Decimal"} {:t "Period"}]
       [[{:t "Para", :c [{:t "Str", :c "foo"}]}]
        [{:t "Para", :c [{:t "Str", :c "bar"}]}]]]}]

    [:pandocir.test/bulletlist
     {:t "BulletList",
      :c
      [[{:t "Para", :c [{:t "Str", :c "foo"}]}]
       [{:t "Para", :c [{:t "Str", :c "bar"}]}]]}]

    [:pandocir.test/definitionlist
     {:t "DefinitionList",
      :c
      [[[{:t "Str", :c "foo"}] [[{:t "Para", :c [{:t "Str", :c "bar"}]}]]]
       [[{:t "Str", :c "fizz"}] [[{:t "Para", :c [{:t "Str", :c "pop"}]}]]]]}]

    [:pandocir.test/header
     {:t "Header",
      :c [2 ["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] [{:t "Str", :c "Head"}]]}]

    [:pandocir.test/row
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      [[["" [] []]
        {:t "AlignRight"}
        2
        3
        [{:t "Para", :c [{:t "Str", :c "bar"}]}]]]]]
    [:pandocir.test/caption
     [[{:t "Str", :c "foo"}] [{:t "Para", :c [{:t "Str", :c "bar"}]}]]]
    [:pandocir.test/tablehead
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] []]]]]
    [:pandocir.test/tablebody
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      3
      [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] []]]
      [[["id'" ["kls'"] [["k1" "v1"] ["k2" "v2"]]] []]]]]
    [:pandocir.test/tablefoot
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      [[["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]] []]]]]
    [:pandocir.test/cell
     [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
      {:t "AlignLeft"}
      1
      1
      [{:t "Para", :c [{:t "Str", :c "bar"}]}]]]
    [:pandocir.test/rowspan
     1]
    [:pandocir.test/colspan
     1]

    [:pandocir.test/table
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
            [{:t "Plain", :c [{:t "Str", :c "footdefault"}]}]]]]]]]}]

    [:pandocir.test/figure
     {:t "Figure",
      :c
      [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
       [[{:t "Str", :c "hello"}] [{:t "Para", :c [{:t "Str", :c "cap content"}]}]]
       [{:t "Para", :c [{:t "Str", :c "fig content"}]}]]}]

    [:pandocir.test/div
     {:t "Div",
      :c
      [["id" ["kls"] [["k1" "v1"] ["k2" "v2"]]]
       [{:t "Para", :c [{:t "Str", :c "Hello"}]}]]}]]
  )
