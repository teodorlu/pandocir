(ns pandocir.cli
  (:require
   [babashka.cli :as cli]
   [clojure.edn :as edn]
   [pandocir.core :as pandocir]))

(def converters
  {{:from :raw :to :ir} pandocir/raw->ir
   {:from :ir :to :raw} pandocir/ir->raw
   {:from :ir :to :hiccup} pandocir/ir->hiccup
   {:from :raw :to :hiccup} #(-> % pandocir/raw->ir pandocir/ir->hiccup)})

(def option-spec
  "Spec for CLI options, compatible with babashka.cli/parse-opts"
  {:spec {:from {:desc (str "valid options: " (->> converters keys (map :from) (into #{})))
                 :coerce :keyword
                 :require true}
          :to {:desc (str "valid options: " (->> converters keys (map :to) (into #{})))
               :coerce :keyword
               :require true}}
   :order [:from :to]})

(defn -main [& args]
  (binding [*print-namespace-maps* false]
    (let [opts (cli/parse-opts args option-spec)]
      (if-let [converter (get converters (select-keys opts [:from :to]))]
        (-> *in* slurp edn/read-string converter prn)
        (do
          (println (format "No converter available for converting from %s to %s"
                           (pr-str (:from opts))
                           (pr-str (:to opts))))
          (System/exit 1))))))
