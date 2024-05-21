(ns pandocir.ir
  (:require [clojure.walk :as walk]))

(def ^:private type->ir
  {"Str"         :pandocir.type/str
   "Emph"        :pandocir.type/emph
   "Underline"   :pandocir.type/underline
   "Strong"      :pandocir.type/strong
   "Strikeout"   :pandocir.type/strikeout
   "Superscript" :pandocir.type/superscript
   "Subscript"   :pandocir.type/subscript
   "SmallCaps"   :pandocir.type/small-caps
   "Quoted"      :pandocir.type/quoted
   "Cite"        :pandocir.type/cite
   "Code"        :pandocir.type/code
   "Space"       :pandocir.type/space
   "SoftBreak"   :pandocir.type/soft-break
   "LineBreak"   :pandocir.type/line-break
   "Math"        :pandocir.type/math
   "RawInline"   :pandocir.type/raw-inline
   "Link"        :pandocir.type/link
   "Image"       :pandocir.type/image
   "Note"        :pandocir.type/note
   "Span"        :pandocir.type/span})


(defn ^:private inline-args->ir [ir-type c]
  (case ir-type
    :pandocir.type/str         {:pandocir/text c}

    :pandocir.type/emph        {:pandocir/inlines c}

    :pandocir.type/underline   {:pandocir/inlines c}

    :pandocir.type/strong      {:pandocir/inlines c}

    :pandocir.type/strikeout   {:pandocir/inlines c}

    :pandocir.type/superscript {:pandocir/inlines c}

    :pandocir.type/subscript   {:pandocir/inlines c}

    :pandocir.type/small-caps  {:pandocir/inlines c}

    :pandocir.type/quoted      {:pandocir.quote/type (first c)
                                :pandocir/inlines (second c)}

    :pandocir.type/cite        {:pandocir/citations (first c)
                                :pandocir/inlines (second c)}

    :pandocir.type/code        {:pandocir/attr (first c)
                                :pandocir/text (second c)}

    :pandocir.type/space       {}

    :pandocir.type/soft-break  {}

    :pandocir.type/line-break  {}

    :pandocir.type/math        {:pandocir.math/type (first c)
                                :pandocir/text (second c)}

    :pandocir.type/raw-inline  {:pandocir/format (first c)
                                :pandocir/text (second c)}

    :pandocir.type/link        {:pandocir/attr (first c)
                                :pandocir/inlines (second c)
                                :pandocir/target (last c)}

    :pandocir.type/image       {:pandocir/attr (first c)
                                :pandocir/inlines (second c)
                                :pandocir/target (last c)}

    :pandocir.type/note        {:pandocir/blocks c}

    :pandocir.type/span        {:pandocir/attr (first c)
                                :pandocir/inlines (second c)}))

(defn ^:private inline->ir-1 [{:keys [t c]}]
  (let [ir-type (type->ir t)]
    (merge {:pandocir/type ir-type}
           (inline-args->ir ir-type c))))

(defn inline->ir [inline]
  (walk/postwalk #(if (:t %) (inline->ir-1 %) %) inline))
