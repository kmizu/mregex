package com.github.kmizu.mregex

sealed abstract class Union[+A, +B]
object Union {
  case class Left[A](val value: A) extends Union[A, Nothing]
  case class Right[B](val value: B) extends Union[Nothing, B]
}
