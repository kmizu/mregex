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
}