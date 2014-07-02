(ns process-tree.deps
  (:require [process-tree.utils :refer :all]
            [process-tree.node :refer :all]))

(defn has-dependencies?
  [node]
  (not (empty? (:dependencies node))))

(defn has-dependents?
  [node]
  (not (empty? (:dependents node))))

(defn dependent-keys-for
  [key config]
  (map first
       (filter (fn [[_ v]] (some #(= % key) (compact (:dependencies v))))
                     config)))

(defn build-deps
  ([key config] (build-deps key config '()))
  ([key config dependents]
   (let [node-dependents (map #(build-deps % config) (dependent-keys-for key config))
         node-dependencies (map #(build-deps % config node-dependents) (compact (:dependencies (key config))))
         node (map->ProcessNode (merge
                                  (select-keys (key config) [:name :start])
                                  {:name (name key)
                                   :dependents (concat dependents node-dependents)
                                   :dependencies (concat node-dependencies '())}))]
     (identity node))))

(defn dependencies-for
  ":skype | <#skype> => (<#Xvfb> <#fluxbox> <#skype>)"
  [name config]
  (let [name (if (keyword? name) (identity name) (:name name))
        node (build-deps name config)
        deps (tree-seq has-dependencies? :dependencies node)]
    (reverse deps)))

(defn dependents-for
  ":Xvfb | <#Xvfb> => (<#x11vnc> <#skype> <#fluxbox> <#Xvfb>)"
  [name config]
  (let [name (if (keyword? name) (identity name) (:name name))
        node (build-deps name config)
        deps (tree-seq has-dependents? :dependents node)]
    (reverse deps)))

