(ns teodorlu.pandoc2hiccup
  (:require [cheshire.core :as json]))

(defn a-function [] 42)

(json/parse-string
 "{\"pandoc-api-version\":[1,23,1],\"meta\":{},\"blocks\":[{\"t\":\"Para\",\"c\":[{\"t\":\"Str\",\"c\":\"hi,\"},{\"t\":\"Space\"},{\"t\":\"Emph\",\"c\":[{\"t\":\"Str\",\"c\":\"there\"}]},{\"t\":\"Str\",\"c\":\"!\"}]}]}\n" keyword)

(json/parse-string
 "{\"pandoc-api-version\":[1,23,1],\"meta\":{},\"blocks\":[{\"t\":\"Para\",\"c\":[{\"t\":\"Str\",\"c\":\"hei\"}]},{\"t\":\"Para\",\"c\":[{\"t\":\"Str\",\"c\":\"oslo\"},{\"t\":\"Space\"},{\"t\":\"Str\",\"c\":\"clojure\"}]}]}\n"
 keyword)

{:pandoc-api-version [1 23 1],
 :meta {},
 :blocks
 [{:t "Para", :c [{:t "Str", :c "hei"}]}
  {:t "Para",
   :c [{:t "Str", :c "oslo"} {:t "Space"} {:t "Str", :c "clojure"}]}]}

[:p "hi," " " [:em "there"] "!"]
{:pandoc-api-version [1 23 1],
 :meta {},
 :blocks
 [{:t "Para",
   :c
   [{:t "Str", :c "hi,"}
    {:t "Space"}
    {:t "Emph", :c [{:t "Str", :c "there"}]}
    {:t "Str", :c "!"}]}]}

(defn pandoc-block->hiccup [{:keys [t c]}]
  (case t
    "Space" " "
    "Str" c
    "Emph" (into [:em] (map pandoc-block->hiccup c))
    "Para" (into [:p] (map pandoc-block->hiccup c))))

(defn pandoc->hiccup [{:keys [blocks]}]
  (map pandoc-block->hiccup blocks))

(defn -main [& args]
  ;; (println "hello from pandoc2hiccup")
  ;; (println "got these args:")
  ;; (println (str "\n\n    " (pr-str args) "\n"))
  ;; (println "Now waiting for stdin.")
  (let [in (slurp System/in)]
    (prn (pandoc->hiccup (json/parse-string in keyword)))
    ;; (println "stdin:")
    #_(prn in))
  ;; (println)
  ;; (println "Done.")
  (println "This text was printed for demonstration purposes."))
