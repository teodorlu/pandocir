{
  description = "Development setup for pandocir";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:

    flake-utils.lib.eachDefaultSystem (system:
      let pkgs = import nixpkgs { inherit system; };
      in {
        devShell = pkgs.mkShell {
          nativeBuildInputs = [ pkgs.clojure pkgs.babashka pkgs.bbin ];
          shellHook = ''
            export PATH=$HOME/.local/bin:$PATH
          '';
        };
      });
}
