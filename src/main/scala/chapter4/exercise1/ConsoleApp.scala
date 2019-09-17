package chapter4.exercise1

import java.util.{Timer, TimerTask}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.io.Source

object ConsoleApp extends App {

  def timeout(t: Long, timer: Timer): Future[Unit] = {
    val p = Promise[Unit]
    timer.schedule(new TimerTask {
      def run() = {
        p success ()
        timer.cancel()
      }
    }, t)
    p.future
  }

  private val timeoutTimer = new Timer(false)
  private val ec: ExecutionContext = ExecutionContext.fromExecutor(
    new java.util.concurrent.ForkJoinPool(4)
  )

  println("Please enter url: ")
  val url = scala.io.StdIn.readLine()

  timeoutTimer.scheduleAtFixedRate(new TimerTask {
    def run() = {
      print(".")
    }
  }, 0, 50)

  val f: Future[Unit] = Future {
    val f = Source.fromURL(url)
    try {
      val text = f.getLines.toList
      timeoutTimer.cancel()
      println
      text.foreach(s => println(s))
    } finally {
      f.close()
    }
  }(ec)

  timeout(2000, timeoutTimer).foreach(_ =>
  {
    if (!f.isCompleted)
      println("timeout!")
  })(ec)

  //otherwise we won't see the output: timeoutTimer.cancel will terminate the application
  Thread.sleep(4000)
}
