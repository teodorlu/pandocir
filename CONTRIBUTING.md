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

