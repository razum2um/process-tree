(ns process-tree.utils)

(defn compact
  [coll]
  (if (coll? coll)
    (remove nil? coll)
    (compact (list coll))))

(defn str-all
  "str arguments only if all are present"
  ([] nil)
  ([& args]
   (if (some nil? args)
     nil
     (apply str args))))

(defmacro dbg
  [x]
  `(let [x# ~x]
     (do
       (println '~x "->" x#)
       x#)))

