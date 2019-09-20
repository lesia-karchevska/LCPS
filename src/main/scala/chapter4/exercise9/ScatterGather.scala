package chapter4.exercise9

import java.util.concurrent.atomic.AtomicReference

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

object ScatterGather {

  private def addValueAndTryToComplete[T](value: T, sequence: AtomicReference[Seq[T]], total: Int, pr: Promise[Seq[T]]): Unit = {
    val current = sequence.get
    val next = value +: current
    if (next.size == total) {
      pr success next
    } else {
      if(!sequence.compareAndSet(current, next)) {
        addValueAndTryToComplete(value, sequence, total, pr)
      }
    }
  }

  def scatterGather[T] (tasks: Seq[() => T]): Future[Seq[T]] = {
    val p = Promise[Seq[T]]
    val result = new AtomicReference[Seq[T]](Seq())
    tasks.foreach(task => Future {
      println("Hello from " + Thread.currentThread.getName + " !")
      val t = task()
      addValueAndTryToComplete(t, result, tasks.size, p)
    })
    p.future
  }
}
