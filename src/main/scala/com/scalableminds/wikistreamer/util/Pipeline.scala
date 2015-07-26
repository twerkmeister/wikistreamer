package com.scalableminds.wikistreamer.util

object Pipeline {
  implicit class toPiped[V](value:V) {
    def |>[R] (f : V => R) = f(value)
  }

  implicit class toPipedStream[A](value: Stream[A]) {
    def |>[R] (f : A => R): Stream[R] = value.map(f)
  }
}