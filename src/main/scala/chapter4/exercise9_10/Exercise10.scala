package chapter4.exercise9_10

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Success

object Exercise10 extends App {

  val a = new DAG("a")
  val b = new DAG("b")
  val c = new DAG("c")
  val d = new DAG("d")
  val e = new DAG("e")
  val f = new DAG("f")
  val g = new DAG("g")
  a.edges += b
  b.edges += c
  b.edges += d
  c.edges += e
  d.edges += e
  e.edges += f
  f.edges += g

  val dagFuture = DAG.fold(a, (t: String, seq: Seq[String]) => {
    Thread.sleep(50)
    val res = t +: seq
    println("function f for node " + t + " : " + res.toString)
    res.toString
  })

  Await.result(dagFuture, Duration.Inf)
  dagFuture.value match {
    case Some(Success(seq)) => println(seq)
  }
}
