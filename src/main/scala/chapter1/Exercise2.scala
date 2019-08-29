package chapter1

object Exercise2 {
  //exercise 2 Implement fuse method with the given signature
  def fuse[A, B] (a: Option[A], b: Option[B]): Option[(A, B)] = {
    for {
      inside_a <- a
      inside_b <- b
    } yield (inside_a, inside_b)
  }
}
