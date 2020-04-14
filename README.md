# Exploration template for playing with cljfx

This is a simple setup that aims to ~~lure you into the depths~~ make it easy to start 
using cljfx. Just clone it, boot up a repl and start hacking!

1. Cloning:
   ```sh
   git clone https://github.com/cljfx/exploration-template.git
   cd exploration-template 
   ```
2. Booting up a repl:

   If `leiningen` is your tool of choice for Clojure, you can use your favorite 
   editor/IDE to jack in using provided `project.clj`.
   If you prefer `clj`, you can use provided `deps.edn`.
3. Starting hacking:

   Open [src/cljfx/exploration.clj](src/cljfx/exploration.clj) file and evaluate it: it 
   will open a window managed by cljfx using state in an atom. You can use `(fx-help)` 
   (see comments at the bottom of the file) to learn a bit more about cljfx/javafx.

Other useful links: 
- [cljfx examples](https://github.com/cljfx/cljfx/tree/master/examples)
- [javafx javadoc](https://openjfx.io/javadoc/14/index.html)  