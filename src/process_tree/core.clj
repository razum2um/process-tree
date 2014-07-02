(ns process-tree.core
  (:require [environ.core :refer [env]]
            [process-tree.exec :refer :all]
            [process-tree.find :refer :all]))

(def process-tree-env :process-tree)

(defn run
  "This ensures that all dependencies are up-and-running and spawns
  missing processes. You can also run this scheduled and achieve `runit`-like
  monitoring effect - your processes won't be started twice.

  => (run :skype :x11vnc)
  "
  [& args]
  (let [names (filter keyword? args)
        cfg (or (first (filter map? args)) (env process-tree-env))]
    (map #(start-node (find-node % cfg)) names)))

(defn term
  "Stops process and all dependent children

  => (term :Xvfb)
  "
  [& args]
  (let [names (filter keyword? args)
        cfg (or (first (filter map? args)) (env process-tree-env))]
    (map #(stop-node (find-node % cfg)) names)))

(defn setup-dev
  []
  (ns process-tree.core)
  (use '[clojure.tools.namespace.repl :only [refresh]])
  (use 'process-tree.core)
  (use 'process-tree.deps)
  (use 'process-tree.utils)
  (require '[environ.core :refer [env]])
  (def cfg (env process-tree-env)))

