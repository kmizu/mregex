package com.github.kmizu.mregex

sealed abstract class MatchingMode
object MatchingMode {
  case object Nfa extends MatchingMode
  case object Dfa extends MatchingMode
}
