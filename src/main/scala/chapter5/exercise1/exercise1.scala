package chapter5.exercise1

import chapter5.Timed

object exercise1 extends App {

  class SimpleObject[T] (val x: T, val y: String, val z: T)

  println("Average time is: " + Range(0, 1000).map(i => Timed.timed { val obj = new SimpleObject[Int](i, "Hello!", i)}).sum / 1000)

}
