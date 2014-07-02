[![Build Status][BS img]][Build Status]

# Process Tree

Manage your dependent processes in declarative way

[![Clojars Project](http://clojars.org/process-tree/latest-version.svg)](http://clojars.org/process-tree)

## Usage

Declare your processes in `.lein-env` in project root:

```clojure
{:process-tree {:Xvfb    {:start "Xvfb :0 -screen 0 800x600x16"}
                :fluxbox {:start "fluxbox"
                          :dependencies :Xvfb}
                :skype   {:start "echo username password | skype --pipelogin"
                          :dependencies :fluxbox}
                :x11vnc  {:start "x11vnc -xkb -forever"
                          :dependencies :Xvfb}}}
```

then in your code just:

```clojure
(process-tree.core/run :skype :x11vnc) ;; this blocks
```

This ensures that all dependencies are up-and-running and spawns
missing processes.

You can also stop process and all dependent children by:

```clojure
(process-tree.core/term :Xvfb) ;; this blocks
```

This would send `kill -15` to all leaf-children recurcively first
then their's parents and only then terminate `Xvfb` itself.

Your process tree configuration can be also passed directly to run/term:

```clojure
;; assume cfg is as above: {:Xvfb ..., :fluxbox ...}
(process-tree.core/run :skype :x11vnc cfg)
```

**NOTE** that keys in map must match process name,
if not - just name it like you want, but add `:name` key in it's map.

## Supervising

You can also run this scheduled or just in a loop in a separate thread
and achieve `runit`-like monitoring effect. Be sure, your processes
won't be started twice and it's pretty fast check

```clojure
;; assume everything is running
(time (run :x11vnc :skype)) ;;=> "Elapsed time: 0.022286 msecs"
```

## REPL-development

```clojure
(use 'process-tree.core)
(setup-dev)
```

## Test

```bash
$ lein expectations
# or
$ lein autoexpect
```

## License

Copyright © 2014 Vlad Bokov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[BS img]: https://travis-ci.org/razum2um/process-tree.png
[Build Status]: https://travis-ci.org/razum2um/process-tree

