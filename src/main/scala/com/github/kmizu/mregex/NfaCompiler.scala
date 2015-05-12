package com.github.kmizu.mregex

import scala.collection.mutable

object NfaCompiler {
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
