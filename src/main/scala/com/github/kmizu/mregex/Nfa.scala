package com.github.kmizu.mregex

import scala.collection.mutable

case class Nfa(var start: Int = -1, var end: Int = -1, states: mutable.Buffer[Nfa.State] = mutable.Buffer()) {
  def addState(): Int = {
    states += Nfa.State()
    states.size - 1
  }

  def addTransition(current: Int, input: Char, next: Int): Unit = {
    states(current) = states(current)(input) = next
  }

  def addEpsilon(current: Int, next: Int): Unit = {
    states(current) = states(current)() = next
  }

  def move(stateNums: Set[Int], input: Char): Set[Int] = {
    (for (stateNum <- stateNums) yield {
      states(stateNum).transitions.get(input).getOrElse(Set.empty)
    }).flatten
  }

  def eclosure(stateNum: Int): Set[Int] = {
    var result = Set[Int](stateNum)
    var previous = Set[Int]()
    do {
      previous = result
      for (next <- previous) {
        result ++= states(next).epsilonTransitions
      }
    } while (result.size != previous.size)
    result
  }

  def eclosure(stateNums: Set[Int]): Set[Int] = {
    var result = Set[Int]()
    for (stateNum <- stateNums) {
      result ++= eclosure(stateNum)
    }
    result
  }

  def number: Int = states.size - 1

  def matches(input: String): Boolean = {
    val start = this.start
    val end = this.end
    def go(current: Int, input:String): Boolean = {
      if(input == "" && current == end) {
        true
      } else {
        for(next <- this.states(current).epsilonTransitions) {
          if(go(next, input)) return true
        }
        if(input == "") return false
        val ch = input.charAt(0)
        val rest = input.substring(1)
        for(set <- this.states(current).transitions.get(ch); next <- set) {
          if(go(next, rest)) return true
        }
        false
      }
    }
    go(start, input)
  }
}
object Nfa {
  case class State(epsilonTransitions: Set[Int] = Set(), transitions: Map[Char, Set[Int]] = Map()) {
    def update(input: Char, next: Int): State = {
      val newStransition = transitions.get(input) match {
        case Some(set) => transitions.updated(input, set + next)
        case None => transitions.updated(input, Set(next))
      }
      this.copy(transitions = newStransition)
    }

    def update(next: Int): State = {
      val newEpsilontransition = epsilonTransitions + next
      this.copy(epsilonTransitions = newEpsilontransition)
    }
  }
  object Compiler {
    final val NumberOfAlphabets: Int = Character.MAX_VALUE + 1

    def compile(nfa: Nfa): Dfa = {
      val tables = mutable.Buffer[mutable.Buffer[Int]]()
      val nfa2Dfa = mutable.Map[Set[Int], Int]()
      val unmark = mutable.Buffer[Set[Int]]()
      val finals = mutable.Set[Int]()
      val starts = nfa.eclosure(nfa.start)
      unmark += starts
      nfa2Dfa.put(starts, 0)
      tables +=  mutable.Buffer.fill(NumberOfAlphabets)(-1)
      if (starts.contains(nfa.end)) finals += 0
      while (unmark.nonEmpty) {
        val t = unmark.remove(unmark.size - 1)
        val t2 = nfa2Dfa(t)
        for(sym <- 0 until NumberOfAlphabets) {
          val m = nfa.move(t, sym.asInstanceOf[Char])
          val u = nfa.eclosure(m)
          if(u.nonEmpty) {
            val u2 = nfa2Dfa.get(u) match {
              case Some(us) => us
              case None =>
                val u2 = nfa2Dfa.size
                unmark += u
                tables += mutable.Buffer.fill(NumberOfAlphabets)(-1)
                nfa2Dfa.put(u, u2)
                if (u.contains(nfa.end)) finals += u2
                u2
            }
            tables(t2)(sym) = u2
          }
        }
      }
      val newTables = new Array[Array[Int]](nfa2Dfa.size)
      val start = nfa2Dfa(starts)
      for (stateNum <- nfa2Dfa.values) {
        newTables(stateNum) = tables(stateNum).toArray
      }
      new Dfa(newTables, start, finals.toSet)
    }
  }
}