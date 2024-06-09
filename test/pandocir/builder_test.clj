(ns pandocir.builder-test
  (:require
   [pandocir.builder :as builder]
   [clojure.test :refer [deftest is]]))

(deftest doc-test
  (is (= {:blocks []}
         (builder/doc [])))
  (is (= {:blocks []
          :pandoc-api-version [1 23 1]}
         (builder/doc [] {:pandoc-api-version [1 23 1]})))
  (is (= {:blocks []
          :meta {}}
         (builder/doc [] {:meta {}}))))
