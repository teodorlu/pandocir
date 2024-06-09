(ns pandocir.builder-test
  (:require
   [pandocir.builder :as b]
   [clojure.test :refer [deftest is]]))

(deftest str-test
  (is (= {:pandocir/type :pandocir.type/str, :pandocir/text "Hello,"}
         (b/str "Hello,"))))

(deftest space-test
  (is (= {:pandocir/type :pandocir.type/space}
         (b/space))))

(deftest para-test
  (is (= {:pandocir/type :pandocir.type/para,
          :pandocir/inlines
          [(b/str "Hello,")
           (b/space)]}
         (b/para (b/str "Hello,") (b/space)))))

(deftest emph-test
  (is (= {:pandocir/type :pandocir.type/emph,
          :pandocir/inlines
          [(b/str "Pandoc")]}
         (b/emph (b/str "Pandoc")))))

(deftest doc-test
  (is (= {:blocks []}
         (b/doc {})))
  (is (= {:blocks []
          :pandoc-api-version [1 23 1]}
         (b/doc {:pandoc-api-version [1 23 1]})))
  (is (= {:blocks []
          :meta {}}
         (b/doc {:meta {}})))
  (is (= {:pandoc-api-version [1 23 1],
          :meta {},
          :blocks
          [{:pandocir/type :pandocir.type/para,
            :pandocir/inlines
            [{:pandocir/type :pandocir.type/str, :pandocir/text "Hello,"}
             {:pandocir/type :pandocir.type/space}
             {:pandocir/type :pandocir.type/emph,
              :pandocir/inlines
              [{:pandocir/type :pandocir.type/str, :pandocir/text "Pandoc"}]}
             {:pandocir/type :pandocir.type/str, :pandocir/text "!"}]}]}
         (b/doc {:pandoc-api-version [1 23 1] :meta {}}
                (b/para (b/str "Hello,")
                        (b/space)
                        (b/emph (b/str "Pandoc"))
                        (b/str "!"))))))
