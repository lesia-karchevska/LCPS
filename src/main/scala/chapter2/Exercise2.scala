package chapter2

import java.util.{Timer, TimerTask}

class CancellationObject {
  @volatile var cancel = false
}

object Exercise2 {

  //exercise 2
  def periodically(duration: Long)(b: () =>Unit) = {
    val task = new TimerTask {
      override def run(): Unit = { b() }
    }
    new Timer().schedule(task, duration, duration);
  }

  def periodically2(duration: Long)(b: () => Unit)(cancellation: CancellationObject) = {
    new Thread(() => {
      while(!cancellation.cancel) {
        b()
        Thread.sleep(duration)
      }
    }).start()
  }
}