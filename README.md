**⚠️ THIS PROJECT IS EXPERIMENTAL, EXPECT BREAKING CHANGES ⚠️**

# `pandocir`: Pandoc Intermediate Representation as Clojure data

[Pandoc] is a lovely program for converting documents between formats.
This conversion happens by first converting from a source format to an intermediate representation, then converting from an intermediate representation to the target format.
`pandocir` lets you work with Pandoc IR as Clojure data.

[Pandoc]: https://pandoc.org/

## Use as a library

You can use `pandocir` as a Git dep with `deps.edn`:

``` clojure
io.github.teodorlu/pandocir {:git/sha "GIT SHA"}
```

Replace `"GIT SHA"` with the version you want.
If you don't want to copy Git SHAs by hand, consider using [Neil].

    neil dep add io.github.teodorlu/pandocir

Upgrade to the latest version with:

    neil dep upgrade

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
