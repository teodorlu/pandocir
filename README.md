# `pandoc2hiccup`

An incomplete Babashka script for converting Pandoc JSON to Hiccup.
`pandoc2hiccup` is meant to be used in a Unix pipe after Pandoc:

    $ echo 'hi, _there_!' | pandoc --from markdown --to json | pandoc2hiccup
    [:p "hi, " [:em "there"] "!"]

Currently supports a tiny subset of Pandoc JSON.

## How to run code in this project

- **Run the script locally**

    ```
    echo 'hi, _there_!' | pandoc2hiccup
    ```

    Teodor has installed a development version on his machine.
    New changes are reflected on next script execution.

- **Run the tests**

    ```
    bin/kaocha
    ```

- **Run a JVM REPL**

    ```
    clj
    ```

- **Run a Babashka REPL**

    ```
    bb
    ```

- **Run a JVM/Babashka REPL from your editor**

    Like you'd do normally with Cider, Calva or your tool of choice.

## Local development with https://github.com/lambdaisland/launchpad

The authors like using Launchpad, and recommend new contributors to try it out.
Launchpad is an optional development-only dependency.

To configure Launchpad to start with the `:dev` alias, add the following to your `deps.local.edn` file:

``` clojure
{:launchpad/aliases [:dev]}
```

... then start launchpad with the shim in `bin/`, for example `bin/launchpad --emacs` or `bin/launchpad --vs-code`.
