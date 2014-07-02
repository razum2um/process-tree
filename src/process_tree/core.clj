(ns process-tree.core
  (:require [process-tree.exec :refer :all]
            [process-tree.find :refer :all]))

(defn run
  [& args]
  (map #(-> % find-node start-node) args))

(defn term
  [& args]
  (map #(-> % find-node stop-node) args))

