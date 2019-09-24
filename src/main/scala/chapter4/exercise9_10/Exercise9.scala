package chapter4.exercise9_10

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Success

object Exercise9 extends App {

  def createRandomTask(): () => Int = {
    val random = new scala.util.Random()
    val sleep = random.nextInt(100)
    val value = random.nextInt(500)

    () => {
      Thread.sleep(sleep)
      println("random task produced " + value)
      value
    }
  }

  val future = ScatterGather.scatterGather(Range(0, 15).map(i => createRandomTask()))
  Await.result(future, Duration.Inf)
  future.value match {
    case Some(Success(seq)) => println(seq)
  }

  println("Testing second version of scatterGather that uses flatMap combinator")

  val future_v2 = ScatterGather.scatterGather_v2(Range(0, 15).map(i => createRandomTask()))
  Await.result(future_v2, Duration.Inf)
  future_v2.value match {
    case Some(Success(seq)) => println(seq)
  }
}
