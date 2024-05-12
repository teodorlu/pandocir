# INSTALLING

`pandoc2hiccup` can be installed for local development or from Github.

## Install for local development

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
