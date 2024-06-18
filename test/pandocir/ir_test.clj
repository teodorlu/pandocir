(ns pandocir.ir-test
  (:require [pandocir.ir :as sut]
            [pandocir.pandoc-test-data :refer [pandoc-test-data]]
            [clojure.test :refer [deftest is]]))

(deftest bijective
  (doseq [[test-name test-data] pandoc-test-data]
    (is (= test-data (sut/ir->pandoc (sut/pandoc->ir test-data))))))

(deftest idempotent
  (doseq [[test-name test-data] pandoc-test-data]
    (is (= (sut/pandoc->ir test-data)
           (sut/pandoc->ir (sut/pandoc->ir test-data))))))
