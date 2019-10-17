package com.github.kmizu.mregex

sealed abstract class MatchingStrategy
object MatchingStrategy {
  case object Nfa extends MatchingStrategy
  case object Dfa extends MatchingStrategy
}
