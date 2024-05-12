(ns teodorlu.pandoc2hiccup-test
  (:require
   [clojure.test :refer [deftest is]]
   [teodorlu.pandoc2hiccup :as pandoc2hiccup]
   [babashka.cli]))

(deftest a-function-test
  (is (= 42 (pandoc2hiccup/a-function))))
