(ns pandocir.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [pandocir.core :as pandocir]))

(def hello-pandoc-raw
  {:pandoc-api-version [1 23 1]
   :meta {}
   :blocks [{:c [{:c "Hello,", :t "Str"}
                 {:t "Space"}
                 {:c [{:c "Pandoc", :t "Str"}], :t "Emph"}
                 {:c "!", :t "Str"}],
             :t "Para"}]})

(def hello-pandoc-ir
  {:pandoc-api-version [1 23 1],
    :meta {},
    :blocks
    [{:pandocir/type :pandocir.type/para,
      :pandocir/inlines
      [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello,"}
       {:pandocir/type :pandocir.type/space}
       {:pandocir/type :pandocir.type/emph,
        :pandocir/inlines
        [{:pandocir/type :pandocir.type/str, :pandocir/text "Pandoc"}]}
       {:pandocir/type :pandocir.type/str, :pandocir/text "!"}]}]})

(deftest raw->ir-test
  (is (= hello-pandoc-ir
         (pandocir/raw->ir hello-pandoc-raw))))

(deftest ir->raw-test
  (is (= hello-pandoc-raw
         (pandocir/ir->raw hello-pandoc-ir))))

(deftest ir->hiccup-test
  (is (= '([:p "Hello," " " [:em "Pandoc"] "!"])
         (pandocir/ir->hiccup hello-pandoc-ir))))

#_ (set! *print-namespace-maps* false)
