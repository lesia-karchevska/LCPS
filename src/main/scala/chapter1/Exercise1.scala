package chapter1


private object Exercise1 {

  //exercise 1 Implement compose method with the given signature
  def compose[A, B, C] (g: B => C, f: A => B): A => C =
    (a: A) => { g(f(a)) }
}
