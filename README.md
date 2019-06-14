![JavaSee logo](logo/JavaSee.png)

# JavaSee: Java Code Piece Finder

Query Method Calls from Java Programs.  It's Java version of querly https://github.com/soutaro/querly

## Installation

JavaSee requires Java(JDK) 11 or later and Gradle 5.0 or later.

1. Install JDK 11 or later
2. Install Gradle 5.0 or later
3. Type `$ gradle shadowjar`
4. You can found JavaSee-all.jar on `build/libs/`

## Quick start

As a first step, you can run JavaSee init command to create an example
javasee.yml:

```
$ java -jar build/libs/JavaSee-all.jar init
$ cat javasee.yml
```

```yaml
rules:
  - id: javasee.example
    pattern:
      - _.println(_)
    message: |
      Trap println() method.
```

Next you can run `JavaSee check -config=javasee.yml` command to **JavaSee** patterns against your code base.

```
$ java -jar build/libs/JavaSee-all.jar check -config javasee.yml
```

## Documentation

- [Pattern syntax](doc/pattern-syntax.md)
- [Command line options](doc/command-line-options.md)
