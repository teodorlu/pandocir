# `pandoc2hiccup`

A Babashka script for converting Pandoc JSON to Hiccup.
`pandoc2hiccup` is meant to be used in a Unix pipe after Pandoc:

    echo 'hi, _there_!' | pandoc2hiccup
    [:p "hi, " [:em "there"] "!"]

## Does it work yet?

No!
The README was written before the code.

## System design

The code can be run from a Babashka REPL, from a Clojure JVM REPL or as a standalone script.

`bb.edn` refers to `deps.edn` for dependencies.

Unit tests can be run with Kaocha:

    bin/kaocha

## Install for development

When you install for development, you will get a local `pandoc2hiccup` binary runs code straight from this folder.
Save your changes, then the next `pandoc2hiccup` invocation will run new code.

0. Clone this folder
0. Install Babashka: https://github.com/babashka/babashka
1. Install bbin: https://github.com/babashka/bbin
2. From this folder, run:

   ```
   bbin install .
   ```

Now, run pandoc2hiccup!

    echo 'hi, _there_!' | pandoc2hiccup

If you want a development version next to the previous version, you can install a development version with a different binary name:


```
bbin install . --as pandoc2hiccup-dev
```

## Install from Github

0. Install Babashka: https://github.com/babashka/babashka
1. Install bbin: https://github.com/babashka/bbin
2. Install `pandoc2hiccup` with bbin:

   ```
   bbin install io.github.teodorlu/pandoc2hiccup --latest-sha
   ```

This will install the latest version from the `master` branch.
To update `pandoc2hiccup`, rerun the install command.

## Thank you

Without [@borkdude][borkdude] and [@rads][rads], `pandoc2hiccup` wouldn't exist. Thank you!

[borkdude]: https://github.com/borkdude/
[rads]: https://github.com/rads/
