package chapter5.exercise10

import chapter5.Timed

import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.CollectionConverters._

//the task seems to be too easy for parallel computations to get involved. the cost of merging the results appears to outweight using multiple threads
//parallel version works better when there are more computations
//also, there might be a better version for parallel computations that will work faster in this case as well
object BracketsBalancer extends App {

  class Balance {
    var left: Int = 0
    var right: Int = 0
  }

  //this version with recursive merging is way too slow (much slower than the sequential version)
  def updateBalance(buf: ArrayBuffer[Char], ch: Char): ArrayBuffer[Char] = {
    if (ch.equals('(') || ch.equals(')')) {
      if (ch.equals('(') || buf.size == 0 || buf(buf.size - 1).equals(')')) buf.addOne(ch)
      else if (buf(buf.size - 1).equals('(')) buf.remove(buf.size - 1)
    }
    buf
  }

  def mergeBalance(left: ArrayBuffer[Char], right: ArrayBuffer[Char]): ArrayBuffer[Char] = {
    if (left.size > 0 && right.size > 0 && left(left.size - 1).equals('(') && right(0).equals(')')) { left.remove(left.size - 1); right.remove(0); mergeBalance(left, right) }
    else left.addAll(right)
  }

  //second version
  def updateBalance_v2(bal: Balance, ch: Char): Balance = {
    if (ch.equals('('))  { bal.left +=1 }
    if (ch.equals(')')) { if (bal.left > 0) { bal.left -= 1 } else { bal.right += 1 } }
    bal
  }

  def mergeBalance_v2(leftBal: Balance, rightBal: Balance): Balance = {
    val diff = Math.min(leftBal.left, rightBal.right)
    leftBal.left = leftBal.left + rightBal.left - diff
    leftBal.right = leftBal.right + rightBal.right - diff
    leftBal
  }

  //third version
  def updateBalance_v3(bal: (Int, Int), ch: Char): (Int, Int) = {
    if (ch.equals('('))  { (bal._1 + 1, bal._2) }
    else {
      if (ch.equals(')')) { if (bal._1 > 0) { (bal._1 - 1, bal._2) } else { (bal._1, bal._2 + 1) } }
      else bal
    }
  }

  def mergeBalance_v3(leftBal: (Int, Int), rightBal: (Int, Int)): (Int, Int) = {
    val diff = Math.min(leftBal._1, rightBal._2)
    (leftBal._1 + rightBal._1 - diff, leftBal._2 + rightBal._2 - diff)
  }

  def parallelBalanceParenthesis(s: Seq[Char]) : Boolean ={
    s.par.aggregate(new ArrayBuffer[Char])((buf, ch) => updateBalance(buf, ch), (bufLeft, bufRight) => mergeBalance(bufLeft, bufRight)).size == 0
  }

  def parallelBalanceParenthesis_v2(s: Seq[Char]) : Boolean ={
    val bal = s.par.aggregate(new Balance)((bal, ch) => updateBalance_v2(bal, ch), (balLeft, balRight) => mergeBalance_v2(balLeft, balRight))
    bal.left == 0 && bal.right == 0
  }

  def parallelBalanceParenthesis_v3(s: Seq[Char]) : Boolean ={
    val bal = s.par.aggregate((0, 0): (Int, Int))((bal, ch) => updateBalance_v3(bal, ch), (balLeft, balRight) => mergeBalance_v3(balLeft, balRight))
    bal._1 == 0 && bal._2 == 0
  }

  def sequentialBalanceParenthesis(s: String) : Boolean ={
    s.toSeq.foldLeft(new ArrayBuffer[Char])((buf, ch) => updateBalance(buf, ch)).size == 0
  }

  val str = scala.io.StdIn.readLine()
  println("par: " + Timed.timed { println(parallelBalanceParenthesis(str*10000)) })
  Thread.sleep(1000)
  println("par v2: " + Timed.timed { println(parallelBalanceParenthesis_v2(str*10000)) })
  Thread.sleep(1000)
  println("par v3: " + Timed.timed { println(parallelBalanceParenthesis_v3(str*10000)) })
  Thread.sleep(1000)
  println("seq: " + Timed.timed { println(sequentialBalanceParenthesis(str*10000)) })
}
