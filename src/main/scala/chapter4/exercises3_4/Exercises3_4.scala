package chapter4.exercises3_4

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object Exercises3_4 extends App {

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

  val futureP = new FutureOpPromise[Int](Future.apply({
    Thread.sleep(500)
    throw new Exception
  })(ec))


  futureP.exists(i => 3/i > 0).onComplete(t => t match {
    case Success(v) => println("success: " + v)
    case Failure(f) => println("fail: " + f) //this never happens
  })(ec)

  Thread.sleep(1000)

  val future_v2 = new FutureOp[Int](Future.apply({
    0
  })(ec))

  future_v2.exists(i => 3/i > 0).onComplete(t => t match {
    case Success(v) => println("success: " + v)
    case Failure(f) => println("fail: " + f) //this never happens
  })(ec)

  Thread.sleep(1000)
}
