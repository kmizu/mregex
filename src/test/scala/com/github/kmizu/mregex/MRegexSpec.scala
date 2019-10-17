package com.github.kmizu.mregex

import org.scalatest._

class MRegexSpec extends FlatSpec with Matchers {
  "MRegex using DFA" should "match" in {
    MRegex("a") matches "a" should be (true)
    MRegex("a") matches "b" should be (false)
    MRegex("a|b") matches "a" should be (true)
    MRegex("a|b") matches "b" should be (true)
    MRegex("a|b") matches "c" should be (false)
    MRegex("ab") matches "ab" should be (true)
    MRegex("ab") matches "ac" should be (false)
    MRegex("a*") matches "aa" should be (true)
    MRegex("(a|b)*") matches "ababab" should be (true)
    MRegex("(a|b)*") matches "abababc" should be (false)
  }

 "MRegex using NFA" should "match" in {
    MRegex("a", strategy = MatchingStrategy.Nfa) matches "a" should be (true)
    MRegex("a", strategy = MatchingStrategy.Nfa) matches "b" should be (false)
    MRegex("a|b", strategy = MatchingStrategy.Nfa) matches "a" should be (true)
    MRegex("a|b", strategy = MatchingStrategy.Nfa) matches "b" should be (true)
    MRegex("a|b", strategy = MatchingStrategy.Nfa) matches "c" should be (false)
    MRegex("ab", strategy = MatchingStrategy.Nfa) matches "ab" should be (true)
    MRegex("ab", strategy = MatchingStrategy.Nfa) matches "ac" should be (false)
    MRegex("a*", strategy = MatchingStrategy.Nfa) matches "aa" should be (true)
    MRegex("(a|b)*", strategy = MatchingStrategy.Nfa) matches "ababab" should be (true)
    MRegex("(a|b)*", strategy = MatchingStrategy.Nfa) matches "abababc" should be (false)
 }
}
