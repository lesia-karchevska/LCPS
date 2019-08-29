package chapter1

object Exercise5 {
  //exercise 5 Implement permutations function which, given a string,
  // returns a sequence of strings that are lexicographic permutations of the input string
  def permutations(x: String): Seq[String] = {
    def allocate(s: String, a: Char): Seq[String] = {
      if (s.length() < 1)
        Seq(a.toString())
      else
        List.range(0, s.length() + 1).map(i => { val (fst, snd) = s.splitAt(i); fst + a.toString() + snd })
    }
    def go(acc: Seq[String], remainder: String): Seq[String] = {
      if (remainder.length() < 1)
        acc
      else {
        go(acc.flatMap(sequence => allocate(sequence, remainder.charAt(0))), remainder.substring(1, remainder.length()))
      }
    }
    go(Seq(""), x)
  }
}
