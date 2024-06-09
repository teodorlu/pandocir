(ns pandocir.builder
  "Functions to create Pandoc IR with less typing"
  (:refer-clojure :exclude [str]))

(defn doc
  ([blocks] (doc blocks {}))
  ([blocks {:keys [pandoc-api-version meta]}]
   (cond-> {:blocks blocks}
     pandoc-api-version (assoc :pandoc-api-version pandoc-api-version)
     meta (assoc :meta meta))))

(defn para
  [& inlines]
  {:pandocir/type :pandocir.type/para,
   :pandocir/inlines inlines})

(defn str [s]
  {:pandocir/type :pandocir.type/str, :pandocir/text s})

(defn space []
  {:pandocir/type :pandocir.type/space})
