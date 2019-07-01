# Command line options

JavaSee has the following subcommands: `init`, `check`, `find`, `test`, `help`, and `version`.

## `javasee init`

`javasee init` generates a `javasee.yml` configuration file.
Edit the config file to define your rules.

```
$ javasee init
```

## `javasee check`

`javasee check` checks Java source files specified in the command line arguments.

```
$ javasee check

# Specify the directory to check
$ javasee check src

# Specify the config file and output format
$ javasee check -config config.yml -format json src
```

## `javasee find`

`javasee find` detects the source code which matches the given pattern. You can use this command to find some pattern of Java code included in your project, and to debug the result.

```
$ javasee find "_.println(...)"
```

## `javasee test`

`javasee test` runs unit-tests included in the configuration. Your rules can include `tests` attribute to define unit-tests, and `test` command to execute them.

```
$ javasee test
```

