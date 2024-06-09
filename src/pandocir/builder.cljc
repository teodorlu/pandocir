(ns pandocir.builder
  "Functions to create Pandoc IR with less typing")

(defn doc
  ([blocks] (doc blocks {}))
  ([blocks {:keys [pandoc-api-version meta]}]
   (cond-> {:blocks blocks}
     pandoc-api-version (assoc :pandoc-api-version pandoc-api-version)
     meta (assoc :meta meta))))
