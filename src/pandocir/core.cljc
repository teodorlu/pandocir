(ns pandocir.core
  (:require
   pandocir.ir
   pandocir.hiccup))

(defn raw->ir [rawpandoc]
  (pandocir.ir/pandoc->ir rawpandoc))

(defn ir->raw [ir]
  (pandocir.ir/ir->pandoc ir))

(defn ir->hiccup [ir]
  (apply list (:blocks (pandocir.hiccup/ir->hiccup ir))))
