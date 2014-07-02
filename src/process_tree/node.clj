(ns process-tree.node
  (:require [process-tree.utils :refer :all]
            [clojure.string :refer [join]]))

(defrecord ProcessNode
  [pid name command start dependents dependencies]
  Object
  ;; avoid recurcion by default
  (toString [n]
    (str "#ProcessNode{"
         (join ", " (compact (map #(str-all % ": " (% n)) '(:pid :name :start :command))))
         ", dependencies: [" (join ", " (map :name (:dependencies n)))
         "], dependents: [" (join ", " (map :name (:dependents n))) "]}")))

