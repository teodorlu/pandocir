**⚠️ THIS PROJECT IS EXPERIMENTAL, EXPECT BREAKING CHANGES ⚠️**

# `pandocir`: Pandoc Intermediate Representation as Clojure data

[Pandoc] is a lovely program for converting documents between formats.
This conversion happens by first converting from a source format to an intermediate representation, then converting from an intermediate representation to the target format.
`pandocir` lets you work with Pandoc IR as Clojure data.

[Pandoc]: https://pandoc.org/

## A library spawned from a Clojure/Oslo mob programming session

During [a Babashka Meetup in Oslo], we did some mob programming.
The task was to write a script that converts Pandoc JSON to Hiccup.
Some of us decided to give this code a bit more love.

Note: all of Pandoc JSON is still not supported.
Please consider this library _a start_.

[a Babashka Meetup in Oslo]: https://www.meetup.com/clojure-oslo/events/300614179/

## Use as a library

You can use `pandocir` as a Git dep with `deps.edn`:

``` clojure
io.github.teodorlu/pandocir {:git/sha "GIT SHA"}
```

Replace `"GIT SHA"` with the version you want.
If you don't want to copy Git SHAs by hand, consider using [Neil].

    neil dep add io.github.teodorlu/pandocir

For example usage, see the unit tests.

[Neil]: https://github.com/babashka/neil

## Use as a CLI tool

`pandocir` can be installed as a CLI tool.
You can install `pandocir` with [bbin]:

    bbin install io.github.teodorlu/pandocir --latest-sha

You can now convert

    $ echo 'hi, _there_!' | pandoc --from markdown --to json | pandocir pandoc2hiccup
    [:p "hi, " [:em "there"] "!"]

[bbin]: https://github.com/babashka/bbin

## Local development

We recommend using a JVM REPL for local development.

You may run the tests with Kaocha:

    bin/kaocha

To work on the CLI, we recommend installing a development version with bbin:

    bbin install . --as pandocir-dev

Installed this way, `pandocir-dev` will reflect your current local folder.
