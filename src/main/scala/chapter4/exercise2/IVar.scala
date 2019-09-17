package chapter4.exercise2

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

class IVar[T]{
  private val p = Promise[T]
  def apply(): T = {
    if (p.isCompleted)
      Await.result(p.future, Duration.Inf)
    else
      throw new Exception
  }

  def :=(x: T): Unit = {
      p.success(x)
  }
}
