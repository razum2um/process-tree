(ns process-tree.utils
  (:use [clojure.pprint :only [print-table]])
  (:require [clojure.reflect :as r])
  )

(defmacro obj-to-map
  "Converts a Java object to Clojure map"
  [obj & body]
  (let [afn (fn [[method kw]]
              `(~kw (. ~obj ~method)))]
    `(assoc {} ~@(mapcat afn (partition 2 body)))))

(defn compact
  [coll]
  (if (coll? coll)
    (remove nil? coll)
    (compact (list coll))))

(defmacro dbg
  [x]
  `(let [x# ~x]
     (do
       (println '~x "->" x#)
       x#)))

