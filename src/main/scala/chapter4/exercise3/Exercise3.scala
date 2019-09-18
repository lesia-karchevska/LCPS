package chapter4.exercise3

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Exercise3 extends App {

  private val ec: ExecutionContext = ExecutionContext.fromExecutor(
    new java.util.concurrent.ForkJoinPool(4))

  val future = new FutureOp[Int](Future.apply({
    Thread.sleep(500)
    0
  })(ec))


  future.exists(i => 3/i > 0).onComplete(t => t match {
    case Success(v) => println("success: " + v)
    case Failure(f) => println("fail: " + f)
  })(ec)

  Thread.sleep(1000)
}
