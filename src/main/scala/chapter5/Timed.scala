package chapter5

object Timed {

  //method taken from chapter 5
  @volatile var dummy: Any = _
  def timed[T](body: =>T): Double = {
    val start = System.nanoTime
    dummy = body
    val end = System.nanoTime
    ((end - start) / 1000) / 1000.0
  }
}
