![JavaSee logo](logo/JavaSee.png)
[![Build Status](https://travis-ci.com/sider/JavaSee.svg?token=1b3KJqNfDxzAbTA6x5p4&branch=master)](https://travis-ci.com/sider/JavaSee)

# JavaSee

JavaSee is a fully customizable linter for Java code, where you can define your own rules specific to your project or your company.

```yaml
rules:
  - id: your-company.java.arraylist
    pattern: new ArrayList(...)
    message: In this company, you shouldn't use ArrayList
```

I don't know why the company prohibits using `ArrayList`, but assume a company has such a rule. Probably no linter in the world provides this rule, but you can implement it using JavaSee in three lines of code as you can see in the example above.

Some rules you want are too subtle or too project/team dependent so general lint tools don’t have them. JavaSee offers a new way to access linter rules: you can define your rules with a pattern language, which makes defining new rules 10x easier!

## Installation

It requires Java 11 or later.

You can download the binary distribution from the [releases page](https://github.com/sider/JavaSee/releases).
Unzip the archive, and execute the `javasee` or `javasee.bat` included in the `bin` directory.

```
$ javasee-some-version/bin/javasee
```

You can build from the source code.

```
$ git clone htts://github.com/sider/JavaSee.git
$ cd JavaSee
$ ./gradlew shadowjar
$ java -jar build/libs/JavaSee-all.jar
```

## Quick start

```
$ javasee init          # Generate a config file in your repository.
$ vim javasee.yml       # Edit the config file to define your rules.
$ javasee check         # Run the linter.
```

## Documentation

- [Pattern syntax](doc/pattern-syntax.md)
- [Command line options](doc/command-line-options.md)
- [Config file format](doc/rule.md)
