(ns pandocir.pandoc-runner
  (:require
   [cheshire.core :as json]
   [clojure.java.shell :as sh]
   [clojure.test :refer [deftest is]]
   [hiccup2.core :as hiccup]
   [pandocir.hiccup :refer [ir->hiccup]]
   [pandocir.ir :refer [pandoc->ir]]
   [pandocir.pandoc-test-data :refer [pandoc-test-data]]))

(def test-data (into {} pandoc-test-data))

(defn call-pandoc [input from to]
  (let [{:keys [out err]} (sh/sh "pandoc" "-f" from "-t" to :in input)]
    (when err (print err))
    out))

(defn call-pandoc-with-block [block to]
  (-> {:pandoc-api-version [1 23 1]
       :meta {}
       :blocks [block]}
      (json/encode)
      (call-pandoc "json" to)))

(defn call-pandoc-with-inline [inline to]
  (call-pandoc-with-block {:t "Plain" :c [inline]} to))

(defn pandoc->hiccup [pandoc]
  (ir->hiccup (pandoc->ir pandoc)))

(defn pandoc->hiccup->html [pandoc]
  (str (hiccup/html (pandoc->hiccup pandoc)) "\n"))

(defn block-compare-pandoc-to-hiccup [block]
  (= (call-pandoc-with-block block "html") (pandoc->hiccup->html block)))

(defn inline-compare-pandoc-to-hiccup [inline]
  (= (call-pandoc-with-inline inline "html") (pandoc->hiccup->html inline)))

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
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/code test-data))))
