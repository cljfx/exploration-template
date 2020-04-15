## Exploration template for playing with cljfx

This is a simple setup that aims to ~~lure you into the depths~~ make it easy to start 
using [cljfx](https://github.com/cljfx/cljfx). Just clone it, boot up a repl and start 
hacking!

### Cloning

```sh
git clone https://github.com/cljfx/exploration-template.git cljfx-exploration
cd cljfx-exploration
```

### Booting up a repl

If `leiningen` is your tool of choice for Clojure, you can use your favorite 
editor/IDE to jack in using provided `project.clj`.
If you prefer `clj`, you can use provided `deps.edn`.
   
### Starting hacking

Open [src/cljfx/exploration.clj](src/cljfx/exploration.clj) file and evaluate it: it 
will open a window managed by cljfx using state in an atom. You can use `(fx-help)` 
(see comments at the bottom of the file) to learn a bit more about cljfx/javafx.

### Useful links 
- [cljfx examples](https://github.com/cljfx/cljfx/tree/master/examples)
- [javafx javadoc](https://openjfx.io/javadoc/14/index.html)  
