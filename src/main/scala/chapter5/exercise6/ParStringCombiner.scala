package chapter5.exercise6

import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.Combiner
import scala.collection.parallel.CollectionConverters._

//taken from chapter 5
class ParStringCombiner extends Combiner[Char, ParString] {

  private val chunks = new ArrayBuffer += new StringBuilder
  private var lastc = chunks.last
  var size = 0
  def addOne(elem: Char) = {
    lastc += elem
    size += 1
    this
  }

  def combine[N <: Char, NewTo >: ParString](that: Combiner[N, NewTo]) = {
    if (this eq that) this else that match {
      case that: ParStringCombiner =>
        size += that.size
        chunks ++= that.chunks
        lastc = chunks.last
        this
    }
  }

  def clear = {

  }

  def resultOld: ParString = {
    val rsb = new StringBuilder
    for (sb <- chunks) rsb.append(sb)
    new ParString(rsb.toString)
  }
  //end: taken from chapter 5

  //improvement: parallelize appending the chunks
  def result: ParString = {
    new ParString(chunks.par.aggregate(new StringBuilder())((seq, el) => seq.append(el), (seq1, seq2) => seq1.append(seq2)).toString)
  }
}
