# Command line options

JavaSee has subcommands: `init`, `check`, `find`, `test`, `help`, and `version`.

## `init`

`init` subcommand generate default `javasee.yml` config file.  It is used when creating new config file.

```
# Generate javasee.yml under current directory
java -jar JavaSee-all.jar init
```

## `check`

`check` subcommand analayzes all Java codes under specified directories and print the matching lines.


```
# Analyze all Java codes using `javasee.yml` under the current directory.
java -jar JavaSee-all.jar check -config javasee.yml
```

```
# Analyze all Java codes using `javasee.yml` under `src`, which is specified by -root option
java -jar JavaSee-all.jar check -config javasee.yml -root src
```

```
# Analyze all Java coces under `src` and print the result in JSON mode (-format json)
java -jar JavaSee-all.jar check -config javasee.yml -root src -format json
```

## `find`

`find` subcommand analyzes all Java codes under specified directories using pattern provied by command-line argument and print the matching line.

```
# Analyze all Java codes under current directory and print lines matching with println(), provided by command-line argument.
java -jar JavaSee-all.jar find "println(_)" .
```

## `test`

`test` subcommand analyzes all Java codes under specified directories and print the matching or not-matching lines.

```
# Analyze all Java codes using `javasee.yml` under `src`, which is specified by -root option
# and print the result in text mode
java -jar JavaSee-all.jar check -config javasee.yml -root src
```

## `help`

`help` subcommand shows usage of JavaSee.

```
# Show usage of JavaSee
java -jar JavaSee-all.jar` help
```

## `version`

`version` subcommand shows the version of running JavaSee.

```
# Show usage of JavaSee
java -jar JavaSee-all.jar` version
```

