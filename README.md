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

### Exploration idea

If you don't have anything in particular in mind, you could try building an app that 
generates a sequence of random numbers and shows them as a line chart:
1. Add a `:button` with a `:text` and `:on-action` handler that puts a sequence of random
numbers to state atom;
2. Add a chart that shows this sequence of random numbers (see 
[chart examples](https://github.com/cljfx/cljfx/blob/master/examples/e14_charts.clj));

You can use layout containers like `:v-box` or `:h-box` to place UI elements like charts
and buttons next to each other.

For extra points, instead of a button that generates series of random numbers you can use 
a [text field to input a number](https://github.com/cljfx/cljfx/blob/master/examples/e29_text_formatter.clj#L27-L31) 
and generate Collatz Conjecture sequence from that number. The Collatz Conjecture can be 
summarized as follows:
> Take any positive integer n. If n is even, divide n by 2. If n is odd, multiply n by 3 
> and add 1. Repeat the process until 1 is reached. The conjecture states that no matter
> which number you start with, you will always reach 1 eventually.