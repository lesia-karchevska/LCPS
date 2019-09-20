package chapter4.exercise9

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
      value
    }
  }

  val future = ScatterGather.scatterGather(Range(0, 15).map(i => createRandomTask()))
  Await.result(future, Duration.Inf)
  future.value match {
    case Some(Success(seq)) => println(seq)
  }
}
