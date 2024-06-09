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

The mob programming resulted in one commit:

https://github.com/teodorlu/pandocir/commit/13475492accd6de0b682ed677bbf9969e04d47ec

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

## Local development

Please read [DEVELOPING.md]

[DEVELOPING.md]: DEVELOPING.md
