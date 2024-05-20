**⚠️ THIS PROJECT IS EXPERIMENTAL, EXPECT BREAKING CHANGES ⚠️**

# `pandocir`: Pandoc Intermediate Representation as Clojure data

[Pandoc] is a lovely program for converting documents between formats.
This conversion happens by first converting from a source format to an intermediate representation, then converting from an intermediate representation to the target format.
`pandocir` lets you work with Pandoc IR as Clojure data.

[Pandoc]: https://pandoc.org/

## Use as a library

Put

``` clojure
io.github.teodorlu/pandocir {:git/sha "GIT SHA"}
```

in your `deps.edn`, replacing `"GIT SHA"` with the version you want.
If you don't want to do this by hand, consider using [Neil].

    neil dep add io.github.teodorlu/pandocir

, then upgrade to the latest version with

    neil dep upgrade

.

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

## CLI tool for local development

Installing from github with `bbin` will not automatically update the script.
To install a local development version (which is always up-to-date), run

    bbin install . --as pandocir-dev

from this folder.
Your local `pandocir-dev` binary will now always be in sync with the code in this folder.

## Local development

This repository aims to give a no-surprises local development experience.
Start a REPL like you normally would, run the tests like you normally would.
