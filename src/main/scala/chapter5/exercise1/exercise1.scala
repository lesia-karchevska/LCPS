package chapter5.exercise1

object exercise1 extends App {

  class SimpleObject[T] (val x: T, val y: String, val z: T)

  @volatile var dummy: Any = _
  def timed[T](body: =>T): Double = {
    val start = System.nanoTime
    dummy = body
    val end = System.nanoTime
    ((end - start) / 1000) / 1000.0
  }

  println("Average time is: " + Range(0, 1000).map(i => timed { val obj = new SimpleObject[Int](i, "Hello!", i)}).sum / 1000)

}
