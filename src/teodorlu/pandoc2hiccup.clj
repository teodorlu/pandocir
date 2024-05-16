(ns teodorlu.pandoc2hiccup
  (:require [cheshire.core :as json]))

(defn pandoc-block->hiccup [{:keys [t c]}]
  (case t
    "Space" " "
    "Str" c
    "Emph" (into [:em] (map pandoc-block->hiccup c))
    "Para" (into [:p] (map pandoc-block->hiccup c))))

(defn pandoc->hiccup [{:keys [blocks]}]
  (map pandoc-block->hiccup blocks))

(defn -main [& _args]
  (let [in (slurp System/in)]
    (prn (pandoc->hiccup (json/parse-string in keyword)))))
