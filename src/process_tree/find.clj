(ns process-tree.find
  (:require [process-tree.deps :refer :all]
            [process-tree.node :refer :all])
  (:import (com.jezhumble.javasysmon JavaSysMon)))

(defn filter-process
  [node process]
  (or
    (= (:pid  node) (-> process .processInfo .getPid))
    (= (:name node) (-> process .processInfo .getName))))

(defn process-root
  []
  (.find (.processTree (JavaSysMon.)) 1))

(defn process-subtree
  ([p]
   (tree-seq
     #(not (-> % .children .isEmpty))
     #(into '() (.children %))
     p)))

(defn process-tree
  ([] (process-subtree (process-root)))
  ([p] (process-subtree p)))

(defn find-process
  [node]
  (let [dependencies (map find-process (:dependencies node))
        dependents (map find-process (:dependents node))]
    (assoc (merge node
                  (process-to-map
                    (first
                      (filter
                        (partial filter-process node)
                        (process-tree)))))
           :dependencies
           dependencies
           :dependents
           dependents)))

(defn find-node
  "(find-node :xvfb) -> ProcessNode"
  [name config]
  (find-process (build-deps name config)))

