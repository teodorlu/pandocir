# DEVELOPING PANDOCIR

Most development work requires that you have installed a JVM, Clojure and Babashka.

## Run the tests once

    bin/kaocha

## Run the tests in watch mode

    bin/kaocha --watch

## Run static analysis

    bb lint

Static analysis requires that you have installed clj-kondo locally.
See https://github.com/clj-kondo/clj-kondo for installation instructions.

## Run the slower tests

    bb pandoc-tests

`pandoc-tests` requires that you have `pandoc` on your `PATH`.
For Pandoc installation instructions, see https://pandoc.org/.
