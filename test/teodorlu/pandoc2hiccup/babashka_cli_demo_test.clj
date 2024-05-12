(ns teodorlu.pandoc2hiccup.babashka-cli-demo-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [babashka.cli]))

(deftest demonstrate-babashka-cli
  (testing "babashka.cli/parse-opts is nice when you want quick support for long options"
    (is (= {:from "markdown", :to "json"}
           (babashka.cli/parse-opts ["--from" "markdown" "--to" "json"]))))
  (testing "for scripts with subcommands, consider using babashka.cli/dispatch."
    (let [apples-return-value "there are apples"
          oranges-return-value "oranges give vitamin C!"
          fruit-dispatch-table [{:cmds ["apples"]
                                 :fn (fn [_opts+args] apples-return-value)}
                                {:cmds ["oranges"]
                                 :fn (fn [_opts+args] oranges-return-value)}]]
      (is (= apples-return-value
             (babashka.cli/dispatch fruit-dispatch-table ["apples"])))
      (is (= oranges-return-value
             (babashka.cli/dispatch fruit-dispatch-table ["oranges"])))))
  (testing "subcommands can also receive options. Option value types are inferred."
    (is (= 42
           (babashka.cli/dispatch [{:cmds ["mycmd"]
                                    :fn (fn [opts+args]
                                          (:count (:opts opts+args)))}]
                                  ["mycmd" "--count" "42"]))))
  (testing "opts+args is just a map."
    (is (map? (babashka.cli/dispatch [{:cmds ["mycmd"] :fn identity}]
                                     ["mycmd" "--count" "42"])))))
