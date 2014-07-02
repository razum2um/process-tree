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
    (map #(-> % (partial find-node cfg) start-node) names)))

(defn term
  "Stops process and all dependent children

  => (term :Xvfb)
  "
  [& args]
  (let [names (filter keyword? args)
        cfg (or (first (filter map? args)) (env process-tree-env))]
    (map #(-> % (partial find-node cfg) stop-node) names)))

