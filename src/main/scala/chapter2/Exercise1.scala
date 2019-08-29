package chapter2

import java.util.concurrent.CountDownLatch

class Result[T] {
  var res: T = _
}

object Exercise1 {

  def thread[T](body: =>T)(result: Result[T])(l: CountDownLatch)(name: String): Thread = {
    val t = new Thread {
      override def run() = { result.res =  body; println("executing " + Thread.currentThread.getName); l.countDown() }
    }
    t.setName(name)
    t.start()
    t
  }

  //exercise 1
  def parallel[A, B](a: =>A, b: =>B): (A, B) = {

    val latch = new CountDownLatch(2)
    val resA = new Result[A]
    val resB = new Result[B]

    val tA = thread(a)(resA)(latch)("A")
    val tB = thread(b)(resB)(latch)("B")

    latch.await()
    (resA.res, resB.res)
  }
}

object ex extends App {

  //exercise 1
  lazy val x = {Thread.sleep(50); 2}
  lazy val y = {Thread.sleep(35); 3}
  Exercise1.parallel(x, y)
}
