package chapter1

object Exercise6 extends App {

  //exercise 6
  def combinations(x: Seq[Int], k: Int): Seq[Seq[Int]] = {
    def lowerBound(s: Seq[Int], n: Int): Int = s match {
      case Nil => n
      case x+:_ => x - 1
    }
    def getAllocations(alloc: Int, k: Int, lb: Int): Seq[Int] = List.range(k - alloc, lb + 1)

    def go(allocated: Int, n: Int, k: Int, current: Int, acc: Seq[Seq[Int]]): Seq[Seq[Int]] = {
      current match {
        case 0 => acc.map(el => el.map(i => x(i - 1)))
        case current => {
          go(allocated + 1, n, k, current - 1, acc.flatMap(s => getAllocations(allocated, k, lowerBound(s, n)).map(y => y +: s)))
        }
      }
    }
    go(0, x.size, k, k, Seq(Seq()))
  }

}


