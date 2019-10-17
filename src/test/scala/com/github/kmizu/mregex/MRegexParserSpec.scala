package com.github.kmizu.mregex

import org.scalatest._
import Ast._
import MRegex.Parser._

import scala.collection.mutable.Buffer

class MRegexParserSpec extends FlatSpec with Matchers {
  "MRegex.Parser" should "parses the following input strings successfully" in {
    parse("a") should be (Alphabet('a'))
    parse("(a)") should be (Alphabet('a'))
    parse("ab") should be (Concat(Alphabet('a'), Alphabet('b')))
    parse("a*") should be (KleeneStar(Alphabet('a')))
    parse("a|b") should be (Choice(Alphabet('a'), Alphabet('b')))
    parse(" *") should be (KleeneStar(Alphabet(' ')))
  }

  "MRegex.Compiler" should "compiles the following pattern and matches the string" in {
   import MRegex.Compiler._
    matches(parse("a"), "a") should be (true)
    matches(parse("a"), "b") should be (false)
    matches(parse("a|b"), "a") should be (true)
    matches(parse("a|b"), "b") should be (true)
    matches(parse("a|b"), "c") should be (false)
    matches(parse("ab"), "ab") should be (true)
    matches(parse("ab"), "ac") should be (false)
    matches(parse("a*"), "aa") should be (true)
    matches(parse("(a|b)*"), "ababab") should be (true)
    matches(parse("(a|b)*"), "abababc") should be (false)
  }

  "Nfa.Compiler" should "translates an NFA to the corresponding DFA using subset construction algorithm" in {
    val nfa = Nfa(0, 3, Buffer(
      Nfa.State(transitions = Map('a' -> Set(1))),
      Nfa.State(epsilonTransitions = Set(2), transitions = Map('a' -> Set(2))),
      Nfa.State(transitions = Map('b' -> Set(3))),
      Nfa.State()
    ))
    val dfa = Nfa.Compiler.compile(nfa)
    dfa.matches("aab") should be (true)
    dfa.matches("ab") should be (true)
    dfa.matches("abc") should be (false)
  }
}
