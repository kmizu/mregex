package com.github.kmizu.mregex
import com.github.kmizu.mregex.Ast._

object RegexCompiler {
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

  def run(nfa: Nfa, input: String): Boolean = {
    val start = nfa.start
    val end = nfa.end
    def go(current: Int, input:String): Boolean = {
      if(input == "" && current == end) {
        true
      } else {
        for(next <- nfa.states(current).epsilonTransitions) {
          if(go(next, input)) return true
        }
        if(input == "") return false
        val ch = input.charAt(0)
        val rest = input.substring(1)
        for(set <- nfa.states(current).transitions.get(ch); next <- set) {
          if(go(next, rest)) return true
        }
        false
      }
    }
    go(start, input)
  }

  def matches(exp: Exp, input: String): Boolean = {
    val nfa = compile(exp)
    val dfa =  NfaCompiler.compile(nfa)

    //run(nfa, input)
    dfa.matches(input)
  }
}
