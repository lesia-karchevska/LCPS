package chapter2.exercises3_6

class Consumer[T](v: SyncVar[T])(f:SyncVar[T] => Unit) {

  def consume(): Unit = {
    new Thread(() => {
      while (!v.cancel) {
        v.getSyncLock().synchronized {
          if (v.nonEmpty()) {
            f(v)
          } else {
            println("busy-waiting for input...")
          }
        }
      }
    }).start()
  }
}
