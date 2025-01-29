(ns pandocir.table-test
  (:require [clojure.test :refer [deftest is testing]]
            [pandocir.ir :as ir]
            [pandocir.table :as table]))

(def table-xy-1234
  {:t "Table"
   :c [["" [] []]
       [nil []]
       [[{:t "AlignDefault"} {:t "ColWidthDefault"}]
        [{:t "AlignDefault"} {:t "ColWidthDefault"}]]
       [["" [] []]
        [[["" [] []]
          [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "x" :t "Str"}] :t "Plain"}]]
           [["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "y" :t "Str"}] :t "Plain"}]]]]]]
       [[["" [] []]
         0
         []
         [[["" [] []]
           [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "1" :t "Str"}] :t "Plain"}]]
            [["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "2" :t "Str"}] :t "Plain"}]]]]
          [["" [] []]
           [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "3" :t "Str"}] :t "Plain"}]]
            [["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "4" :t "Str"}] :t "Plain"}]]]]]]]
       [["" [] []] []]]})

(def table-xywz-12
  {:t "Table"
   :c [["" [] []]
       [nil []]
       [[{:t "AlignDefault"} {:t "ColWidthDefault"}]
        [{:t "AlignDefault"} {:t "ColWidthDefault"}]]
       [["" [] []]
        [[["" [] []]
          [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "x" :t "Str"}] :t "Plain"}]]
           [["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "y" :t "Str"}] :t "Plain"}]]]]
         [["" [] []]
          [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "w" :t "Str"}] :t "Plain"}]]
           [["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "z" :t "Str"}] :t "Plain"}]]]]]]
       [[["" [] []]
         0
         []
         [[["" [] []]
           [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "1" :t "Str"}] :t "Plain"}]]
            [["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "2" :t "Str"}] :t "Plain"}]]]]]]]
       [["" [] []] []]]})

(def ir-plain-x
  [{:pandocir/type :pandocir.type/plain
    :pandocir/inlines
    [{:pandocir/type :pandocir.type/str :pandocir/text "x"}]}])

(def ir-plain-y
  [{:pandocir/type :pandocir.type/plain
    :pandocir/inlines
    [{:pandocir/type :pandocir.type/str :pandocir/text "y"}]}])

(deftest head->ir
  (testing "One header row one cell"
    (is (= (table/head->ir
            [["" [] []]
             [[["" [] []]
               [[["" [] []] {:t "AlignDefault"} 1 1 ir-plain-x]]]]])
           {:pandocir.table/header-rows
            [[{:pandocir/type :pandocir.type/cell}]]})))

  (testing "Two header rows one cell in each"
    (is (= (table/head->ir
            [["" [] []]
             [[["" [] []]
               [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "x" :t "Str"}] :t "Plain"}]]]]
              [["" [] []]
               [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "w" :t "Str"}] :t "Plain"}]]]]]])
           {:pandocir.table/header-rows
            [[{:pandocir/type :pandocir.type/cell}]
             [{:pandocir/type :pandocir.type/cell}]]})))

  (testing "Two header rows two cells in each"
    (is (= (table/head->ir
            [["" [] []]
             [[["" [] []]
               [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "x" :t "Str"}] :t "Plain"}]]
                [["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "y" :t "Str"}] :t "Plain"}]]                ]]
              [["" [] []]
               [[["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "z" :t "Str"}] :t "Plain"}]]
                [["" [] []] {:t "AlignDefault"} 1 1 [{:c [{:c "w" :t "Str"}] :t "Plain"}]]]]]])
           {:pandocir.table/header-rows
            [[{:pandocir/type :pandocir.type/cell} {:pandocir/type :pandocir.type/cell}]
             [{:pandocir/type :pandocir.type/cell} {:pandocir/type :pandocir.type/cell}]]}))
    ))



















(comment
  (ir/pandoc->ir table-xy-1234)
  (require '[pandocir.ir :as ir])
  (set! *print-namespace-maps* false)
  )

{:pandocir.caption/short nil
 :pandocir.table/head
 [["" [] []]
  [[["" [] []]
    [[["" [] []]
      {:t "AlignDefault"}
      1
      1
      [{:pandocir/type :pandocir.type/plain
        :pandocir/inlines
        [{:pandocir/type :pandocir.type/str :pandocir/text "x"}]}]]
     [["" [] []]
      {:t "AlignDefault"}
      1
      1
      [{:pandocir/type :pandocir.type/plain
        :pandocir/inlines
        [{:pandocir/type :pandocir.type/str :pandocir/text "y"}]}]]]]]]
 :pandocir/type :pandocir.type/table
 :pandocir.attr/keyvals []
 :pandocir.caption/blocks []
 :pandocir.table/foot [["" [] []] []]
 :pandocir.table/body
 [[["" [] []]
   0
   []
   [[["" [] []]
     [[["" [] []]
       {:t "AlignDefault"}
       1
       1
       [{:pandocir/type :pandocir.type/plain
         :pandocir/inlines
         [{:pandocir/type :pandocir.type/str :pandocir/text "1"}]}]]
      [["" [] []]
       {:t "AlignDefault"}
       1
       1
       [{:pandocir/type :pandocir.type/plain
         :pandocir/inlines
         [{:pandocir/type :pandocir.type/str :pandocir/text "2"}]}]]]]
    [["" [] []]
     [[["" [] []]
       {:t "AlignDefault"}
       1
       1
       [{:pandocir/type :pandocir.type/plain
         :pandocir/inlines
         [{:pandocir/type :pandocir.type/str :pandocir/text "3"}]}]]
      [["" [] []]
       {:t "AlignDefault"}
       1
       1
       [{:pandocir/type :pandocir.type/plain
         :pandocir/inlines
         [{:pandocir/type :pandocir.type/str :pandocir/text "4"}]}]]]]]]]
 :pandocir.attr/classes []
 :pandocir.attr/id ""
 :pandocir.table/col-specs
 [[{:t "AlignDefault"} {:t "ColWidthDefault"}]
  [{:t "AlignDefault"} {:t "ColWidthDefault"}]]}
