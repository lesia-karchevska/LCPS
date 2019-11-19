package chapter6.exercise1

import rx.Subscriber

object Exercise1 extends App {

  val ct = new CancellationToken()
 // val obs = ThreadDetectorObservable.createThreadObservable(ct)
  val sub = new Subscriber[Thread]() {
    override def onNext(t: Thread) = { println("Thread " + t.getName + " started!") }
    override def onCompleted(): Unit = { println("Completed!") }
    override def onError(e: Throwable): Unit = { println("Error!") }
  }

//  val s = obs.subscribe(sub)

  Thread.sleep(5000)
 // s.unsubscribe()
 // ct.cancel = true
  println("Shutting down")
  Thread.sleep(500)
}
