package com.github.kmizu.mregex

object Ast {
  sealed abstract class Exp
  case object Empty extends Exp
  case class Alphabet(c: Char) extends Exp
  case class Concat(a: Exp, b: Exp) extends Exp
  case class Choice(a: Exp, b: Exp) extends Exp
  case class KleeneStar(a: Exp) extends Exp
}
