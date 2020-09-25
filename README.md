![JavaSee logo](logo/JavaSee.png)

# JavaSee

JavaSee is a fully customizable linter for Java code, where you can define your own rules specific to your project or your company in YAML.

For example, you can write your `javasee.yml` file as follow:

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

### Docker images

We provide Docker images to help you trying JavaSee without installing Java 11.

- https://hub.docker.com/r/sider/javasee

```
$ docker pull sider/javasee
$ docker run -t --rm -v `pwd`:/work sider/javasee
```

The default `latest` tag points to the latest released version.
You can pick a tag from version names from [tags list](https://hub.docker.com/r/sider/javasee/tags).

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

## Releasing

1. Update the [changelog](CHANGELOG.md) and the [version](build.gradle).
2. Run the command for test: `./gradlew clean build`
3. Commit the updated files via `./gradlew commit`
4. Open a new pull request including the commit and merge it when the CI passes.
5. Create a tag for the commit via `./gradlew tag`
6. Push the commit and tag, e.g. `git push --follow-tags`
7. Edit and publish the [draft release](https://github.com/sider/JavaSee/releases).
