# DEVELOPING PANDOCIR

Most development work requires a JVM, Clojure and Babashka.

## Run the tests once

    bin/kaocha

## Run the tests in watch mode

    bin/kaocha --watch

## Run static analysis

    bb lint

Static analysis requires a local clj-kondo installation.
See https://github.com/clj-kondo/clj-kondo for installation instructions.

## Run the slower tests

    bb pandoc-tests

`pandoc-tests` requires that you have `pandoc` on your `PATH`.
For Pandoc installation instructions, see https://pandoc.org/.

## Install the local CLI

A local CLI is provided to convert from/to certain formats.

    $ echo '{:blocks [{:c [1 ["hellopandoc-lol" [] []] [{:c "HelloPandoc", :t "Str"}]], :t "Header"}], :meta {}, :pandoc-api-version [1 23 1]}' | pandocir --from raw --to hiccup
    ([:h1 {:id "hellopandoc-lol"} "HelloPandoc"])

The CLI can be installed for local development with [bbin].
To install the CLI, run

    bbin install .

from the repository root.

[bbin]: https://github.com/babashka/bbin
