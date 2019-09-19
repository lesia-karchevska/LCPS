package chapter4.exercise8
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

class PromiseCompose[T](self: Promise[T]) {

  def compose[S](f: S => T): Promise[S] = {
    val p = Promise[S]
    p.future.onComplete(tryS => {
      tryS match {
        case Success(value) => { self.trySuccess(f(value)) }
        case Failure(exception) => { self.tryFailure(exception) }
      }
    })
    p
  }
}
