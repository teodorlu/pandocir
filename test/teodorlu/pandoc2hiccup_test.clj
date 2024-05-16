(ns teodorlu.pandoc2hiccup-test
  (:require
   [clojure.test :refer [deftest is]]
   [teodorlu.pandoc2hiccup :as pandoc2hiccup]
   [babashka.cli]))

(deftest space-test
  (is (= " " (pandoc2hiccup/pandoc-block->hiccup {:t "Space"}))))

(deftest string-test
  (is (= "there"
         (pandoc2hiccup/pandoc-block->hiccup {:t "Str", :c "there"}))))

(deftest emph-test
  (is (= [:em "there"]
         (pandoc2hiccup/pandoc-block->hiccup
          {:t "Emph", :c [{:t "Str", :c "there"}]}))))

(deftest para-test
  (is (= [:p "hi," " " [:em "there"] "!"]
         (pandoc2hiccup/pandoc-block->hiccup
          {:t "Para",
           :c
           [{:t "Str", :c "hi,"}
            {:t "Space"}
            {:t "Emph", :c [{:t "Str", :c "there"}]}
            {:t "Str", :c "!"}]}))))

(deftest hiccup-test
  (is (= '([:p "hei"] [:p "oslo" " " "clojure"])
         (pandoc2hiccup/pandoc->hiccup
          {:pandoc-api-version [1 23 1],
           :meta {},
           :blocks
           [{:t "Para", :c [{:t "Str", :c "hei"}]}
            {:t "Para",
             :c [{:t "Str", :c "oslo"} {:t "Space"} {:t "Str", :c "clojure"}]}]}))))
