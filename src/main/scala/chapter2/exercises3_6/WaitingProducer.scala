package chapter2.exercises3_6

class WaitingProducer[T] (s: Sync[T]){

  def produce(range: List[T]): Unit = {
    new Thread(() => {
      range.foreach(r => {
        s.putWait(r)
        Thread.sleep(100)
      })
      s.cancel = true
      s.getSyncLock().synchronized {
        s.getSyncLock().notifyAll()
      }
    }).start()
  }
}
