Basically, Java expressions are valid JavaSee patterns. JavaSee provides following patterns:

- `_`: any single expression
- `...`: varargs in method parameters.
- `foo`: identifier `foo`.
- `new Name(x, y)`: new expression instantiating a object of class `Name`. `x` and `y` are parameters.
- `receiver.method(x, y)`: method call expression.  The receiver is `receiver` and method name is `method`.
- `reciever.field`: field access expression. The receiver is `receiver` and field name is `field`.
- `100`: integer literal describing 100.
- `:int:`: any integer literal.
- `1.5`: double literal describing 1.5.
- `:double:` any double literal.
- `true`: true.
- `false`: false.
- `:boolean:` any boolean literal.
- `"foo"`: string literal `"foo"`.
- `:String:`: any String literal.
- `this`: this pattern.
- `null`: null pattern.
- `->`: lambda pattern.
- `x + y`: addition.
- `x - y`: subtraction.
- `x * y`: multiplication.
- `x / y`: division.
- `x % y`: remainder.
- `x > y`: greater than.
- `x >= y`: greater or equal.
- `x < y`: less than.
- `x <= y`: less or equal.
- `x == y`: equal.
- `x != y`: not equal.
- `x << y`: left shift.
- `x >> y`: signed rifht shift.
- `x >>> y`: unsigned rifht shift.
- `x instanceof type`: instance of operator (Currently, type must not be wildcard).
- `!x`: negation.
- `x++`: postfix increment.
- `y++`: postfix decrement.

Sevral Java expression patterns are not supported yet.  They will be supported in the future version.
