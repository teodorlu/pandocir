(ns teodorlu.pandoc2hiccup)

(defn -main [& args]
  (println "hello from pandoc2hiccup")
  (println "got these args:")
  (println (str "\n\n    " (pr-str args) "\n"))
  (println "Now waiting for stdin.")
  (let [in (slurp System/in)]
    (println "stdin:")
    (prn in))
  (println)
  (println "Done.")
  (println "This text was printed for demonstration purposes."))
