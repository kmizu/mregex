package com.github.kmizu.mregex

import java.io.{PrintWriter, StringWriter}

class Dfa(val table: Array[Array[Int]], val start: Int, val finals: Set[Int]) {
  import NfaCompiler.NumberOfAlphabets
  def and(rhs: Dfa): Dfa = {
    val newTable = Array.ofDim[Int](table.length * rhs.table.length, NumberOfAlphabets)
    val newStart = rhs.table.length * start + rhs.start
    var newFinals = Set[Int]()
    for (a <- finals) {
      for (b <- rhs.finals) {
        newFinals += (rhs.table.length * a + b)
      }
    }
    var a = 0
    while (a < table.length) {
      var b = 0
      while (b < rhs.table.length) {
        var input: Int = 0
        while (input < NumberOfAlphabets) {
          val nextA = table(a)(input)
          val nextB = rhs.table(b)(input)
          if (nextA == -1 || nextB == -1) {
            newTable(rhs.table.length * a + b)(input) = -1
          } else {
            newTable(rhs.table.length * a + b)(input) = rhs.table.length * nextA + nextB
          }
          input += 1
        }
        b += 1
      }
      a += 1
    }
    new Dfa(newTable, newStart, newFinals)
  }

  def matches(input: String): Boolean = {
    var current = start
    var cursor = 0
    while(cursor < input.length &&  (current != -1 || !finals.contains(current))) {
      val ch = input.charAt(cursor)
      current = table(current)(ch)
      cursor += 1
    }
    finals.contains(current)
  }


  def disjoint(dfa: Dfa): Boolean = this.and(dfa).isEmpty

  def isEmpty: Boolean = {
    val reachable = mark(Set(), start)
    reachable.filter{finals contains _}.isEmpty
  }

  override def toString: String = {
    var maxDigit: Int = String.valueOf(table.length).length
    if (maxDigit % 2 == 0) maxDigit += 1
    val ASCII_PRINTABLE_START = 32
    val ASCII_PRINTABLE_FINAL = 126
    val buff = new StringWriter
    val w = new PrintWriter(buff)
    w.printf("start: %d%n", new Integer(start))
    w.printf("final: ")
    for (f <- finals) {
      w.printf("%0" + maxDigit + "d ", new Integer(f))
    }
    w.println()

    for(i <- 0 until table.length) {
      w.printf("%0" + maxDigit + "d: ", new Integer(i))
      for(j <- ASCII_PRINTABLE_START to ASCII_PRINTABLE_FINAL) {
        if (table(i)(j) != -1) {
          w.printf("%c -> %0" + maxDigit + "d ", j.toChar, new Integer(table(i)(j)))
          w.flush()
        }
        w.flush()
      }
      w.println()
    }
    w.flush()
    new String(buff.getBuffer)
  }

  private def mark(reachable: Set[Int], stateNum: Int): Set[Int] = {
    var result = reachable
    if (reachable.contains(stateNum)) return result
    result += stateNum
    var input = 0
    while (input < NumberOfAlphabets) {
      val next = table(stateNum)(input)
      if (next != -1) result ++= mark(reachable, next)
      input += 1
    }
    result
  }

  private def spacing(w: PrintWriter, n: Int): Unit = {
    w.print(" " * n)
  }
}
