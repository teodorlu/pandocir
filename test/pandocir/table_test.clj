(ns pandocir.table-test
  (:require [clojure.test :refer [deftest is testing]]
            [pandocir.ir :as ir]
            [pandocir.table :as table]))

[["" [] []]
 {:t "AlignDefault"}
 1
 1
 [{:t "Plain",
   :c [{:t "Str", :c "Row1,"}
       {:t "Space"}
       {:t "Str", :c "Col1"}]}]]

{:pandocir/type :pandocir.type/cell,
 :pandocir/blocks
 [{:pandocir/type :pandocir.type/plain,
   :pandocir/inlines
   [{:pandocir/type :pandocir.type/str, :pandocir/text "Row1,"}
    {:pandocir/type :pandocir.type/space}
    {:pandocir/type :pandocir.type/str, :pandocir/text "Col1"}]}]}


(def attr ["" [] []])

(def caption [nil []])

(def colspecs
  [[{:t "AlignDefault"} {:t "ColWidthDefault"}]
   [{:t "AlignDefault"} {:t "ColWidthDefault"}]])

(def table-head
  [["" [] []]
   [[["" [] []]
     [[["" [] []]
       {:t "AlignDefault"}
       1
       1
       [{:t "Plain", :c [{:t "Str", :c "Header1"}]}]]
      [["" [] []]
       {:t "AlignDefault"}
       1
       1
       [{:t "Plain", :c [{:t "Str", :c "Header2"}]}]]]]]])

(def table-body-attrs ["" [] []])

(def row-head-columns-count 0)

(def table-body-intermediate-head [])

(def table-body-intermediate-body
  [[["" [] []]
    [[["" [] []]
      {:t "AlignDefault"}
      1
      1
      [{:t "Plain",
        :c [{:t "Str", :c "Row1,"}
            {:t "Space"}
            {:t "Str", :c "Col1"}]}]]
     [["" [] []]
      {:t "AlignDefault"}
      1
      1
      [{:t "Plain",
        :c [{:t "Str", :c "Row1,"}
            {:t "Space"}
            {:t "Str", :c "Col2"}]}]]]]
   [["" [] []]
    [[["" [] []]
      {:t "AlignDefault"}
      1
      1
      [{:t "Plain",
        :c [{:t "Str", :c "Row2,"}
            {:t "Space"}
            {:t "Str", :c "Col1"}]}]]
     [["" [] []]
      {:t "AlignDefault"}
      1
      1
      [{:t "Plain",
        :c [{:t "Str", :c "Row2,"}
            {:t "Space"}
            {:t "Str", :c "Col2"}]}]]]]])

(def table-body
  [table-body-attrs
   row-head-columns-count
   table-body-intermediate-head
   table-body-intermediate-body])

(def table-bodies
  [table-body])

(def table-foot [["" [] []] []])

(def test-table
  {:t "Table",
   :c [attr                             ; done
       caption                          ; done
       colspecs
       table-head table-bodies table-foot]})

{:pandocir/type
 :pandocir.type/table
 :pandocir.attr/id ""
 :pandocir.attr/classes []
 :pandocir.attr/keyvals []
 :pandocir.caption/blocks []
 }

(deftest temptest
  (is (= test-table
         {:t "Table",
          :c [["" [] []]
              [nil []]
              [[{:t "AlignDefault"} {:t "ColWidthDefault"}]
               [{:t "AlignDefault"} {:t "ColWidthDefault"}]]
              [["" [] []]
               [[["" [] []]
                 [[["" [] []]
                   {:t "AlignDefault"}
                   1
                   1
                   [{:t "Plain", :c [{:t "Str", :c "Header1"}]}]]
                  [["" [] []]
                   {:t "AlignDefault"}
                   1
                   1
                   [{:t "Plain", :c [{:t "Str", :c "Header2"}]}]]]]]]
              [[["" [] []]
                0
                []
                [[["" [] []]
                  [[["" [] []]
                    {:t "AlignDefault"}
                    1
                    1
                    [{:t "Plain",
                      :c [{:t "Str", :c "Row1,"}
                          {:t "Space"}
                          {:t "Str", :c "Col1"}]}]]
                   [["" [] []]
                    {:t "AlignDefault"}
                    1
                    1
                    [{:t "Plain",
                      :c [{:t "Str", :c "Row1,"}
                          {:t "Space"}
                          {:t "Str", :c "Col2"}]}]]]]
                 [["" [] []]
                  [[["" [] []]
                    {:t "AlignDefault"}
                    1
                    1
                    [{:t "Plain",
                      :c [{:t "Str", :c "Row2,"}
                          {:t "Space"}
                          {:t "Str", :c "Col1"}]}]]
                   [["" [] []]
                    {:t "AlignDefault"}
                    1
                    1
                    [{:t "Plain",
                      :c [{:t "Str", :c "Row2,"}
                          {:t "Space"}
                          {:t "Str", :c "Col2"}]}]]]]]]]
              [["" [] []] []]]})))
