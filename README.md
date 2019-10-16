# mregex

A minimal eegex matcher in Scala.  This repository is mainly aimed at learning implementations of regular expressions.

## Usage

```scala
import com.github.kmizu.mregex._

matches(pattern=”””ab”””,  string=“ab”) // true
matches(pattern=”””a|b”””, string=”a”) // true
matches(pattern=”””a|b”””, string=”c”) // false
matches(pattern=”””(a|b)*”””, string=”abbaab”) // true
matches(pattern=”””(a|b)*”””, string=”abaaac”) // false
matches(pattern=”””ab”””,  string=“ab”) // true
matches(pattern=”””a|b”””, string=”a”) // true
matches(pattern=”””a|b”””, string=”c”) // false
matches(pattern=”””(a|b)*”””, string=”abbaab”) // true
matches(pattern=”””(a|b)*”””, string=”abaaac”) // false
```

## Syntax

### Alphabet

*a* is regular expression.  Note that *a* doesn't mean `a` itself but any character.

```
a
b
z
0
9
```

### Choice

`e1|e2` is regular expression where `e1` and `e2` are regular expressions:

```
a|b
a|b|c
```

### Concatenation

`e1e2` is regular expression where `e1` and `e2` are regular expressions:

```
ab
abc
```

### Repetition (0 or more)

`e*` is regular expression where `e` is regular expression:

```
a*
(a|b)*
```

### Repetition (1 ore more)

`e+` is regular expression where `e` is regular expression:

```
a+
(a|b)+
```

### Optional (0 ore 1)

`e?` is regular expression where `e` is regular expression:

```
a+
a(b)?c
```

