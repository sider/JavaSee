# The format of config file (`.yml`)

## The format of config file in `check` subcommand

The format of YAML config file is explained in this section. See the following:

```yaml
rules:
  - id: sample.bad_string_equality_check1
    pattern:
      - "@String == _"
      - _ == @String
    message: |
      String literal must not be an operand of ==

      since it cause unexpected behavior
  - id: sample.bad_string_equality_check2
    pattern:
      - "@String != _"
      - "_ != @String"
    message: |
      String literal must not be an operand of !=

      since it cause unexpected behavior
  - id: sample.bad_int_equality_check1
    pattern: "@int == _"
    message: |
      int literal should not be an left operand of ==

      for readabiliy
  - id: sample.bad_int_equality_check2
    pattern: "@int != _"
    message: |
      int literal should not be an left operand of !=

      for readabiliy
  - id: sample.bad_boolean_equality_check1
    pattern:
      - "@boolean == _"
      - "_ == @boolean"
    message: |
      boolean literal should not be an operand of ==

      since it is meaningless in general
```

- `rules`  has rule sequence.  The rule consist of:
  - `id`:  is a unique identifier in this file. It is used to show the message
  - `pattern`: is single pattern string or pattern string sequence.  See [pattern-syntax.md](pattern-syntax.md)
  - `message`: is shown when `pattern` matches some Java expressions.
  
## The format of config file in `test` subcommand

The format is very similar with the format of config file in `check`.  It is differ from the `check`s:

- it has `tests` attribute, sequence of test
- `tests` has `match` attribute: this attribute represents expected pattern 
- `tests` has `unmatch` attribute:  this attribute represents not-expected pattern
- `tests` has `justification` attribute: a message

## Import config files

A config file can import other config files.  For example, the following YAML file
imports `foo.yml` and `bar.yml` as config files.  The imported config files are
same as usual config files.  It is usesul to reuse rules.

Note that `rules` cannot be omitted. 

- `javasee.yml`

```yaml
rules: []
import:
  - foo.yml
  - bar.yml
```

- `foo.yml`

```yaml
rules: 
  - id: foo
    pattern: foo-pattern
    message: Foo
```

- `bar.yml`

```yaml
rules: 
  - id: bar
    pattern: bar-pattern
    message: Bar
```
