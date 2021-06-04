## Author Notes

Hannah Bulmer

20714790 hkbulmer

openjdk version "16.0.1" 2021-04-20
OpenJDK Runtime Environment AdoptOpenJDK-16.0.1+9 (build 16.0.1+9)
OpenJDK 64-Bit Server VM AdoptOpenJDK-16.0.1+9 (build 16.0.1+9, mixed mode, sharing)

Computer running: macOS Mojave v10.14.6

## Instructions

Run `make build` in this directory to compile

Run `./rename [flags]` to run program

Usage: `rename [-option argument1 argument2 ...]`

Options:

`-f|file [filename]`          :: file(s) to change (includes directories)

`-p|prefix [string]  `        :: rename `[filename]` so that it starts with `[string]`.

`-s|suffix [string]  `        :: rename `[filename]` so that it ends with `[string]`.

`-r|replace [str1] [str2] `   :: rename `[filename]` by replacing all instances of `[str1]` with `[str2]`.

`-h|help `                    :: print out this help and exit the program.