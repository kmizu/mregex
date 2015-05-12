package com.github.kmizu.mregex

import com.github.kmizu.mregex.Ast._
import scala.util.parsing.combinator._
import scala.util.parsing.input
import input._
import java.io._

object RegexParser extends Parsers {
  type Elem = Char
  private val any: Parser[Char] = elem(".", c => c != CharSequenceReader.EofCh)

  private def chr(c: Char): Parser[Char] = c

  lazy val Regex: Parser[Exp] = Expression <~ END_OF_FILE

  lazy val Expression: Parser[Exp] = rep1sep(Sequence, BAR) ^^ { ns =>
    val x :: xs = ns; xs.foldLeft(x) { (a, y) => Choice(a, y)}
  }
  lazy val Sequence: Parser[Exp] = Suffix.+ ^^ { ns =>
    val x :: xs = ns; xs.foldLeft(x) { (a, y) => Concat(a, y)}
  }
  lazy val Suffix: Parser[Exp] = (
    Prefix <~ QUESTION ^^ { case e => Choice(e, Empty)}
  | Prefix <~ STAR ^^ { case e => KleeneStar(e)}
  | Prefix <~ PLUS ^^ { case e => Concat(e, KleeneStar(e))}
  | Prefix
  )
  lazy val Prefix: Parser[Exp] = Primary
  lazy val Primary: Parser[Exp] = (
    OPEN ~> Expression <~ CLOSE
  | Literal
  )
  lazy val Literal: Parser[Exp] = CHAR ^^ {
    case c => Alphabet(c)
  }
  lazy val CHAR: Parser[Char] = not(BAR | QUESTION | STAR | OPEN | CLOSE) ~> any

  lazy val BAR = chr('|')
  lazy val QUESTION = chr('?')
  lazy val STAR = chr('*')
  lazy val PLUS = chr('+')
  lazy val OPEN = chr('(')
  lazy val CLOSE = chr(')')
  lazy val EXCLAMATION = chr('!')
  lazy val AMPERSAND = chr('&')

  lazy val END_OF_FILE = not(any)

  def parse(input: String): Exp = {
    Regex(StreamReader(new StringReader(input))) match {
      case Success(node, _) => node
      case Failure(msg, rest) =>
        throw new Exception(msg)
      case Error(msg, rest) =>
        throw new Exception(msg)
    }
  }
}


