package com.github.kmizu.mregex

import org.scalatest._

class MRegexSpec extends FlatSpec with Matchers {
  "MRegex using DFA mode" should "match" in {
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

 "MRegex using NFA mode" should "match" in {
    MRegex("a", mode = MatchingMode.Nfa) matches "a" should be (true)
    MRegex("a", mode = MatchingMode.Nfa) matches "b" should be (false)
    MRegex("a|b", mode = MatchingMode.Nfa) matches "a" should be (true)
    MRegex("a|b", mode = MatchingMode.Nfa) matches "b" should be (true)
    MRegex("a|b", mode = MatchingMode.Nfa) matches "c" should be (false)
    MRegex("ab", mode = MatchingMode.Nfa) matches "ab" should be (true)
    MRegex("ab", mode = MatchingMode.Nfa) matches "ac" should be (false)
    MRegex("a*", mode = MatchingMode.Nfa) matches "aa" should be (true)
    MRegex("(a|b)*", mode = MatchingMode.Nfa) matches "ababab" should be (true)
    MRegex("(a|b)*", mode = MatchingMode.Nfa) matches "abababc" should be (false)
 }
}
