# mregex

[![Gitter](https://badges.gitter.im/kmizu/mregex.svg)](https://gitter.im/kmizu/mregex?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Build Status](https://travis-ci.org/kmizu/mregex.png?branch=master)](https://travis-ci.org/kmizu/mregex)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.kmizu/mregex_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.kmizu/mregex_2.13)
[![Scaladoc](http://javadoc-badge.appspot.com/com.github.kmizu/mregex_2.13.svg?label=scaladoc)](http://javadoc-badge.appspot.com/com.github.kmizu/mregex_2.13/index.html#com.github.kmizu.mregex.package)
[![Reference Status](https://www.versioneye.com/java/com.github.kmizu:mregex_2.13/reference_badge.svg?style=flat)](https://www.versioneye.com/java/com.github.kmizu:mregex_2.13/references)

A minimal regex matcher in Scala.  This repository is mainly aimed at learning implementations of regular expressions.

## Usage

Add the following lines to your build.sbt file:

```scala
libraryDependencies += "com.github.kmizu" %% "mregex" % "0.1.0"
```

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

