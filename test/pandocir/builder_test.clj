(ns pandocir.builder-test
  (:require
   [pandocir.builder :as builder]
   [clojure.test :refer [deftest is]]))

(deftest str-test
  (is (= {:pandocir/type :pandocir.type/str, :pandocir/text "Hello,"}
         (builder/str "Hello,"))))

(deftest space-test
  (is (= {:pandocir/type :pandocir.type/space}
         (builder/space))))

(deftest para-test
  (is (= {:pandocir/type :pandocir.type/para,
          :pandocir/inlines
          [(builder/str "Hello,")
           (builder/space)]}
         (builder/para (builder/str "Hello,") (builder/space)))))

(deftest doc-test
  (is (= {:blocks []}
         (builder/doc {})))
  (is (= {:blocks []
          :pandoc-api-version [1 23 1]}
         (builder/doc {:pandoc-api-version [1 23 1]})))
  (is (= {:blocks []
          :meta {}}
         (builder/doc {:meta {}}))))
