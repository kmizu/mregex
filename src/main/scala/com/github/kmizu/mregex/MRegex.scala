package com.github.kmizu.mregex

import java.io.StringReader

import com.github.kmizu.mregex.Ast.{Alphabet, Choice, Concat, Empty, Exp, KleeneStar}

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{CharSequenceReader, StreamReader}

case class MRegex(pattern: String, strategy: MatchingStrategy = MatchingStrategy.Dfa) {
  private[this] lazy val automaton: Union[Nfa, Dfa] = {
    val exp = MRegex.Parser.parse(pattern)
    val nfa = MRegex.Compiler.compile(exp)
    strategy match {
      case MatchingStrategy.Nfa => Union.Left(nfa)
      case MatchingStrategy.Dfa => Union.Right(Nfa.Compiler.compile(nfa))
    }
  }
  def matches(string: String): Boolean = automaton match {
    case Union.Left(nfa) => nfa.matches(string)
    case Union.Right(dfa) => dfa.matches(string)
  }
}
object MRegex {
  object Compiler {
    def compile(exp: Exp): Nfa = {
      val context: Nfa = new Nfa()
      def go(e: Exp): (Int, Int) = e match {
        case Alphabet(ch) =>
          val start = context.addState()
          val end = context.addState()
          context.addTransition(start, ch, end)
          (start, end)
        case KleeneStar(e) =>
          val start = context.addState()
          val end = context.addState()
          val (fst, snd) = go(e)
          context.addEpsilon(start, fst)
          context.addEpsilon(start, end)
          context.addEpsilon(snd, fst)
          context.addEpsilon(snd, end)
          (start, end)
        case Choice(a, b) =>
          val start = context.addState()
          val end = context.addState()
          val resultL = go(a)
          val resultR = go(b)
          context.addEpsilon(start, resultL._1)
          context.addEpsilon(start, resultR._1)
          context.addEpsilon(resultL._2, end)
          context.addEpsilon(resultR._2, end)
          (start, end)
        case Concat(a, b) =>
          val resultL = go(a)
          val resultR = go(b)
          context.addEpsilon(resultL._2, resultR._1)
          (resultL._1, resultR._2)
        case Empty =>
          val start = context.addState()
          val end = context.addState()
          context.addEpsilon(start, end)
          (start, end)
      }
      val (start, end) = go(exp)
      context.start = start
      context.end = end
      context
    }


    def matches(exp: Exp, input: String): Boolean = {
      val nfa = compile(exp)
      val dfa =  Nfa.Compiler.compile(nfa)

      //run(nfa, input)
      dfa.matches(input)
    }
  }
  object Parser extends Parsers {
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
}