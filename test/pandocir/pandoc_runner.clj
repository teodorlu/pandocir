(ns pandocir.pandoc-runner
  (:require
   [babashka.fs :as fs]
   [cheshire.core :as json]
   [clojure.java.shell :as sh]
   [clojure.string :as s]
   [clojure.test :refer [deftest is]]
   [hiccup2.core :as hiccup]
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
      (call-pandoc "html" "json")))

(defn call-pandoc-with-inline [inline]
  (call-pandoc-with-block {:t "Plain" :c [inline]}))

(defn pandoc->hiccup [pandoc]
  (ir->hiccup (pandoc->ir pandoc)))

(defn pandoc->hiccup->html [pandoc]
  (call-pandoc (str (hiccup/html (pandoc->hiccup pandoc))) "html" "json"))

(defn block-compare-pandoc-to-hiccup [block]
  (= (call-pandoc-with-block block) (pandoc->hiccup->html block)))

(defn inline-compare-pandoc-to-hiccup [inline]
  (= (call-pandoc-with-inline inline) (pandoc->hiccup->html inline)))

(deftest comparing-inline-with-pandoc
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
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/link test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/image test-data)))
  ;; Postponing footnote
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/note test-data)))
  (is (inline-compare-pandoc-to-hiccup (:pandocir.test/span test-data))))

(deftest comparing-block-with-pandoc
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/plain test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/para test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/line-block test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/code-block test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/raw-block test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/block-quote test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/ordered-list test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/bullet-list test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/definition-list test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/header test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/horizontal-rule test-data)))
  ;; Postponing tables
  ;; (is (block-compare-pandoc-to-hiccup (:pandocir.test/table test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/figure test-data)))
  (is (block-compare-pandoc-to-hiccup (:pandocir.test/div test-data))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn run-pandoc-tests [_]
  (let [this-ns (symbol (namespace ::sym))]
    (when-not (fs/which "pandoc")
      (throw (ex-info "These tests require pandoc. Please make sure `pandoc` is on your PATH." {})))
    (let [{:keys [fail error] :as report}
          (clojure.test/run-tests this-ns)]
      (when-not (zero? (+ fail error))
        (throw (ex-info (str "Test failures in " this-ns)
                        (assoc report :ns this-ns)))))))

#_ (run-pandoc-tests {})
