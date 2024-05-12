# `pandoc2hiccup`

A Babashka script for converting Pandoc JSON to Hiccup.
`pandoc2hiccup` is meant to be used in a Unix pipe after Pandoc:

    echo 'hi, _there_!' | pandoc2hiccup
    [:p "hi, " [:em "there"] "!"]

## Does it work yet?

No!
The README was written before the code.

## How to run code in this project

- **Run the script locally**

    ```
    echo 'hi, _there_!' | pandoc2hiccup

    ```

    Teodor has installed a development version on his machine.
    New changes are reflected

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
