# Pattern syntax

Basically, Java expressions are valid JavaSee patterns. JavaSee accepts following patterns:

- `_`: any single expression
- `...`: varargs in method parameters.
- `foo`: identifier `foo`.
- `new Name(x, y)`: new expression instantiating a object of class `Name`. `x` and `y` are parameters
- `receiver.method(x, y)`: method call expression.  The receiver is `receiver` and method name is `method`
- `reciever.field`: field access expression. The receiver is `receiver` and field name is `field`
- `100`: integer literal describing 100
- `@int`: any integer literal
- `1.5`: double literal describing 1.5
- `@double:` any double literal
- `true`: true
- `false`: false
- `@boolean:` any boolean literal
- `"foo"`: string literal `"foo"`
- `@String:`: any String literal
- `this`: this pattern
- `null`: null pattern
- `->`: lambda pattern
- `x + y`: addition
- `x - y`: subtraction
- `x * y`: multiplication
- `x / y`: division
- `x % y`: remainder
- `x > y`: greater than
- `x >= y`: greater or equal
- `x < y`: less than.
- `x <= y`: less or equal
- `x == y`: equal
- `x != y`: not equal
- `x << y`: left shift
- `x >> y`: signed rifht shift
- `x >>> y`: unsigned rifht shift
- `~x`: bitwise complement
- `x & y`: bitwise and
- `x | y`: bitwise or
- `x ^ y`: xor
- `x && y`: conditional-and
- `x || y`: conditional-or
- `x ? y : z`: conditional-expression
- `!x`: not
- `x++`: postfix increment.
- `x--`: postfix decrement.
- `++x`: prefix increment
- `--x`: prefix decrement
- `x instanceof type`: instance of operator (currently, type must not be wildcard)
- `v = y`: simple assignment
- `v += y`: compound assignment using `+`
- `v -= y`: compound assignment using `-`
- `v *= y`: compound assignment using `*`
- `v /= y`: compound assignment using `/`
- `v %= y`: compound assignment using `%`
- `v &= y`: compound assignment using `&`
- `v ^= y`: compound assignment using `^`
- `new Object#[x]`: array creation
  - `new Object#[10]#[20]`: multi-dimentional aray
  - Note that this pattern is different from Java's corresponding syntax
- `class[TypeName]`: class literal such as `TypeName.class`

While most Java expression patterns are supported, some Java expression patterns are not supported yet.  
For example, method reference expressions(such as `System.out::println`) and cast 
expressions (such as (`(Integer)obj`)) are not supported yet. They will be supported in the future.

## Usage by example

- Find `System.out.println()` calls:
  - `System.out.println()`, `System.out.println(1)`, etc.

```
System.out.println(...)
```


- Find all usage of `receiver.println()` calls:

```
_.println(...)
```

- Find all usage of `println()` calls:
  - `print("A")`, `print("B")`, etc.

```
println(...)
```


- Find all usage of `+` operators:
  - `1 + 1`, `2.5 + 3.5`, `"A" + "B"`, etc.
  - Note that types of operands are not considered

```
_ + _
```

- Find `instanceof Integer` :
  - `a instanceof Integer`, `b instanceof Integer`, etc.
  
```
_ instanceof Integer
```

- Find all occurence of `instanceof`
  - `_ instanceof _`

```
_ instanceof _
```
