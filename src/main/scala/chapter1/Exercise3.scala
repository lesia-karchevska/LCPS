package chapter1

object Exercise3 {
  //exercise 3 Implement a check method that takes a set of values of type T and a function of type T => Boolean
  //the method returns true if and only if pred returns true for all values in xs without throwing an exception
  def check[T](xs: Seq[T])(pred: T => Boolean): Boolean = {
    def safe_pred(t: T)(pr: T => Boolean): Boolean = {
      try {
        pr(t)
      } catch {
        case ex: Throwable => false
      }
    };
    xs match {
      case Seq() => true
      case head +: tail => safe_pred(head)(pred) && check(tail)(pred)
    }
  }
}
