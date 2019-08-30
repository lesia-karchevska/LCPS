package chapter2.exercises3_6

import scala.util.control.Breaks.{break, breakable}

class Producer[T](v: SyncVar[T]) {

  def produce(range: List[T]): Unit = {
    new Thread(() => {
      range.foreach(r => {
        breakable {
          while (!v.cancel) {
            v.getSyncLock().synchronized {
              if (v.isEmpty()) { v.put(r); break() }
            }
            Thread.sleep(100)
          }
        }
      })
      v.cancel = true
    }
    ).start()
  }
}
