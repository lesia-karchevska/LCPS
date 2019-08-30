package chapter2.exercises3_6

class WaitingConsumer[T](s: Sync[T])(f: Option[T] => Unit) {

  def consume(): Unit = {
    new Thread(() => {
      while (!s.cancel) {
        f(s.getWait())
      }
    }).start()
  }
}
