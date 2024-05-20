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

## Local development

This repository aims to give a no-surprises local development experience.
Start a REPL like you normally would, run the tests like you normally would.

In addition, we choose to document how we work with the project locally.

### Teodor's development environment

Teodor uses [launchpad] for local development.
Launchpad is an optional development-only dependency.

[launchpad]: https://github.com/lambdaisland/launchpad

To configure Launchpad to start with the `:dev` alias, add the following to your `deps.local.edn` file:

``` clojure
{:launchpad/aliases [:dev]}
```

... then start launchpad with the shim in `bin/`, for example `bin/launchpad --emacs` or `bin/launchpad --vs-code`.

`clj-reload` is included as a `:dev` dependency.
This enables a very quick testing workflow: one keystroke saves all the files, reloads code and runs all the tests.

For Doom Emacs & CIDER: :

``` emacs-lisp
(defun teod-reload+test ()
  (interactive)
  (projectile-save-project-buffers)
  (cider-interactive-eval "(do (require 'clj-reload.core) (clj-reload.core/reload))")
  (kaocha-runner-run-all-tests))

(map! :g "M-RET" #'teod-reload+test)
```

Replace `(map! :g "M-RET" #'teod-reload+test)` with something else if you're using a different Emacs flavor.

For Visual Studio Code & Calva:

``` json
    {
        "key": "ctrl+[Semicolon]",
        "command": "runCommands",
        "args": {
            "commands": [
                "workbench.action.files.saveFiles",
                {
                    "command": "calva.runCustomREPLCommand",
                    "args": {
                        "snippet": "(do (require 'clj-reload.core) (clj-reload.core/reload))"
                    }
                },
                {
                    "command": "calva.runCustomREPLCommand",
                    "args": {
                        "snippet": "(flush) #_forces-a-print"
                    }
                },
                "calva.runAllTests"
            ]
        }
    }
```

Replace `"key": "ctrl+[Semicolon]"` with your editor hotkey of choice.
Teodor recommends one that is easy to click (eg ctrl+t, not ctrl+t option+q option+q option+t ctrl+a ctrl+b ctrl+c).
Your local workflow is important!

