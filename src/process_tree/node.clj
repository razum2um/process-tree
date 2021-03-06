(ns process-tree.node
  (:require [clojure.string :refer [join trim]]
            [process-tree.utils :refer [compact str-all]]))

(defrecord ProcessNode
  [pid name command start dependents dependencies]
  Object
  ;; avoid recurcion by default
  (toString [n]
    (str "#ProcessNode{"
         (join ", " (compact (map #(str-all % ": " (% n)) '(:pid :name :start :command))))
         ", dependencies: [" (join ", " (map :name (:dependencies n)))
         "], dependents: [" (join ", " (map :name (:dependents n))) "]}")))

;; avoid recurcion in REPL
(defmethod clojure.core/print-method ProcessNode [x writer]
  (.write writer (str x)))

(defmacro obj-to-map
  "Converts a Java object to Clojure map"
  [obj & body]
  (let [afn (fn [[method kw]]
              `(~kw (. ~obj ~method)))]
    `(assoc {} ~@(mapcat afn (partition 2 body)))))

(defn process-to-map
  "<#JavaProcessInfo> => {:pid ...}"
  [process]
  (if (nil? process)
    (identity {:pid nil})
    (update-in
      (obj-to-map (.processInfo process) getPid :pid getName :name getCommand :command)
      [:command]
      trim)))

(defn process-to-node
  "<#JavaProcessInfo> => <#ProcessNode>"
  [process]
  (when process
    (map->ProcessNode
      (process-to-map process))))

