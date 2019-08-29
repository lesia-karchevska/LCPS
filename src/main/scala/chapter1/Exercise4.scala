package chapter1

//exercise 4 Modify Pair class so that it can be used for pattern matching
case class Pair[P, Q](first: P, second: Q)

object Exercise4 {

  def f[Int](x: Pair[Int, Int]) = {
    x match {
      case Pair(0, _) => println("zero first!")
      case Pair(x, y) => println(x.toString() + y.toString)
    }
  }
}