package chapter4.exercises3_4

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class FutureOp[T](val self: Future[T]) {

  private val ec: ExecutionContext = ExecutionContext.fromExecutor(
    new java.util.concurrent.ForkJoinPool(4)
  )

  def exists(p: T => Boolean): Future[Boolean] = {
    self.transform(tryT => {
      tryT match {
        case Success(value) => {
          try {
            Success(p(value))
          } catch {
            case _ => Success(false)
          }
        }
        case Failure(exception) => Success(false)
      }
    })(ec)
  }

  def exists_v2(p: T => Boolean): Future[Boolean] = {
    self.map(p)(ec).recover({ case _ => false })(ec)
  }
}
