(ns teodorlu.pandoc2hiccup-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [teodorlu.pandoc2hiccup :as pandoc2hiccup]
   [babashka.cli]))

(deftest a-function-test
  (is (= 42 (pandoc2hiccup/a-function))))

(deftest demonstrate-babashka-cli
  (testing "babashka.cli/parse-opts is nice when you want quick support for long options"
    (is (= {:from "markdown", :to "json"}
           (babashka.cli/parse-opts ["--from" "markdown" "--to" "json"]))))
  (testing "for scripts with subcommands, consider using babashka.cli/dispatch."
    (let [fruit-dispatch-table
          [{:cmds ["apples"]
            :fn (fn [_opts] "there are apples")}
           {:cmds ["oranges"]
            :fn (fn [_opts] "oranges give vitamin C!")}]]
      (is (= "there are apples"
             (babashka.cli/dispatch fruit-dispatch-table ["apples"])))))

  )
