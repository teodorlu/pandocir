**⚠️ THIS PROJECT IS EXPERIMENTAL, EXPECT BREAKING CHANGES ⚠️**

# `pandocir`: Pandoc Intermediate Representation as Clojure data

[Pandoc] is a lovely program for converting documents between formats.
This conversion happens by first converting from a source format to an intermediate representation, then converting from an intermediate representation to the target format.
`pandocir` lets you work with Pandoc IR as Clojure data.

[Panodoc]: https://pandoc.org/

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
