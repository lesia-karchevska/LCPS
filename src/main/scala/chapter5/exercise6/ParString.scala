package chapter5.exercise6

import scala.collection.parallel.{Combiner, ParSeq, SeqSplitter}

//the code is taken from chapter 5
class ParString(val str: String) extends ParSeq[Char] {
  def apply(i: Int) = str.charAt(i)
  def length = str.length
  def splitter = new ParStringSplitter(str, 0, str.length)
  def seq = new collection.immutable.WrappedString(str)

  class ParStringSplitter(val s: String, var i: Int, val limit: Int) extends SeqSplitter[Char] {
    final def hasNext = i < limit
    final def next = {
      val r = s.charAt(i)
      i += 1
      r
    }
    def dup = new ParStringSplitter(s, i, limit)
    def remaining = limit - i

    def split = {
      val rem = remaining
      if (rem >= 2) psplit(rem / 2, rem - rem / 2)
      else Seq(this)
    }

    def newCombiner: Combiner[Char, ParString] = new ParStringCombiner

    def psplit(sizes: Int*): Seq[ParStringSplitter] = {
      val ss = for (sz <- sizes) yield {
        val nlimit = (i + sz) min limit
        val ps = new ParStringSplitter(s, i, nlimit)
        i = nlimit
        ps
      }
      if (i == limit) ss.toSeq
      else (ss :+ new ParStringSplitter(s, i, limit)).toSeq
    }
  }


}
