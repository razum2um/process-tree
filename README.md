# Process Tree

Manage your dependent processes in declarative way

[![Build Status][BS img]][Build Status]

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
(process-tree.core/run :skype :x11vnc)
```

This ensures that all dependencies are up-and-running and spawns
missing processes. You can also run this scheduled and achieve `runit`-like
monitoring effect - your processes won't be started twice.

You can also stop process and all dependent children by:

```clojure
(process-tree.core/term :Xvfb)
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

## License

Copyright © 2014 Vlad Bokov

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[BS img]: https://travis-ci.org/razum2um/process-tree.png
[Build Status]: https://travis-ci.org/razum2um/process-tree

