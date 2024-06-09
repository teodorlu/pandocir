(ns pandocir.builder
  "Functions to create Pandoc IR with less typing"
  (:refer-clojure :exclude [str]))

(defn ^:private ir? [m]
  (contains? m :pandocir/type))

(defn ^:private parse-opts+args
  "Treats the first arg as an option map if it's a map, otherwise returns an empty
  option map and an arg vector.

  This allows us to write well-defined variadic functions that can take an optional first-argument option map."
  [args]
  (let [[opts args]
        (if (and (map? (first args))
                 (not (ir? (first args))))
          [(first args) (drop 1 args)]
          [{} args])]
    [opts (or args [])]))

(defn doc
  {:arglists '([opts? & args])}
  ([& args]
   (let [[opts blocks] (parse-opts+args args)
         {:keys [pandoc-api-version meta]} opts]
     (cond-> {:blocks blocks}
       pandoc-api-version (assoc :pandoc-api-version pandoc-api-version)
       meta (assoc :meta meta)))))

(defn para
  [& inlines]
  {:pandocir/type :pandocir.type/para,
   :pandocir/inlines inlines})

(defn str [s]
  {:pandocir/type :pandocir.type/str, :pandocir/text s})

(defn space []
  {:pandocir/type :pandocir.type/space})
