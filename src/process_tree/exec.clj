(ns process-tree.exec
  (:require [process-tree.find :refer :all]
            [process-tree.utils :refer :all]))

(defn exec
  [cmd]
  (.exec (Runtime/getRuntime) (into-array ["/bin/sh" "-c" cmd])))

(defn start-node
  [node]
  (if (nil? (:pid node))
    (do
      (let [new-deps (doall (map start-node (:dependencies node)))]
        (do
          (exec (:start node))
          (loop [new-node (find-process node)]
            (if (nil? (:pid new-node))
              (do
                ;; (pprint (str "wait for" new-node))
                (Thread/sleep 100)
                (recur (find-process node)))))
          (assoc (find-process node) :dependencies new-deps))))
    (find-process node)))

(defn sigterm
  [pid]
  (.exec (Runtime/getRuntime)
         (into-array ["/bin/kill" "-15" (str pid)])))

(defn stop-node
  [node]
  (let [pid (:pid node)]
    (do (doall (map stop-node (dbg (:dependents node))))  ;; halt children
        (sigterm pid)                               ;; halt self
        (loop [new-node (find-process node)]
          (if (some? (dbg (:pid new-node)))
            (do
              (clojure.pprint/pprint (str "wait for term: " new-node))
              (Thread/sleep 100)
              (recur (find-process node)))))
        )))

