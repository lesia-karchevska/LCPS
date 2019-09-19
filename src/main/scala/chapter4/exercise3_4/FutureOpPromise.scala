package chapter4.exercise3_4

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

class FutureOpPromise[T](self: Future[T]) {

  private val ec: ExecutionContext = ExecutionContext.fromExecutor(
    new java.util.concurrent.ForkJoinPool(4)
  )

  def exists(pred: T => Boolean): Future[Boolean] = {
    val p = Promise[Boolean]
    self.onComplete(tryT => {
      tryT match {
        case Success(v) =>  try {
          p success pred(v)
        } catch {
          case _ => p success false
        }
        case Failure(exception) => p success false
      }
    })(ec)
    p.future
  }
}
