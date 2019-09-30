package chapter5.exercise6

import chapter5.Timed

object exercise6 extends App {

  //Oddly enough, ParString works slower than ordinary string, even if we wrap the string into sequence
  val txt = ("Some string " * 25).toSeq
  val partxt = new ParString("Some string " * 25)
  val stime = Timed.timed { txt.filter(_ != ' ') }

  val ptime = Timed.timed { partxt.filter(_ != ' ') }

  println(stime)
  println(ptime)

}
