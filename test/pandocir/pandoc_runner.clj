(ns pandocir.pandoc-runner
  (:require
   [cheshire.core :as json]
   [clojure.java.shell :as sh]
   [clojure.string :as s]
   [clojure.test :refer [deftest is]]
   [hiccup2.core :as hiccup]
   [hickory.core :as hickory]
   [pandocir.hiccup :refer [ir->hiccup]]
   [pandocir.ir :refer [pandoc->ir]]
   [pandocir.pandoc-test-data :refer [pandoc-test-data]]))

(def test-data (into {} pandoc-test-data))

(defn call-pandoc [input from to]
  (let [{:keys [out err]} (sh/sh "pandoc" "-f" from "-t" to :in input)]
    (when err (print err))
    (s/trim out)))

(defn call-pandoc-with-block [block]
  (-> {:pandoc-api-version [1 23 1]
       :meta {}
       :blocks [block]}
      (json/encode)
      (call-pandoc "json" "html")
      (hickory/parse)
      (hickory/as-hickory)))

(defn call-pandoc-with-inline [inline]
  (call-pandoc-with-block {:t "Plain" :c [inline]}))

(defn pandoc->hiccup [pandoc]
  (ir->hiccup (pandoc->ir pandoc)))

(defn pandoc->hiccup->html [pandoc]
  (hickory/as-hickory (hickory/parse (str (hiccup/html (pandoc->hiccup pandoc))))))

(defn block-compare-pandoc-to-hiccup [block]
  (= (call-pandoc-with-block block) (pandoc->hiccup->html block)))

(defn inline-compare-pandoc-to-hiccup [inline]
  (= (call-pandoc-with-inline inline) (pandoc->hiccup->html inline)))

(deftest comparing-with-pandoc
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/str test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/emph test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/underline test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/strikeout test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/superscript test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/subscript test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/smallcaps test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/quoted test-data)))
  ;; Postponing cite
  ;; (is (inline-compare-pandoc-to-hiccup (:pandocir.test/cite test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/code test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/space test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/softbreak test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/linebreak test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/rawinline test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/link test-data))))
