(ns pandocir.table)

(defn head->ir [[attr header-rows]]
  {:pandocir.table/header-rows
   (mapv (fn [[_attr cells]]
           (mapv (fn [cell] {:pandocir/type :pandocir.type/cell})
                 cells))
         header-rows)})
