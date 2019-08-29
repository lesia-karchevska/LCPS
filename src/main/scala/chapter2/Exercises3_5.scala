package chapter2

import scala.util.control.Breaks.{break, breakable}

object Exercises3_6 extends App {

  val sv = new SyncVar[Int]
  val cons = new Consumer(sv)(sv => println(sv.get().toString()))
  val prod = new Producer(sv)

  prod.produce(List.range(0, 15))
  cons.consume()

  val wsv = new SyncVar[Int]
  val wcons = new WaitingConsumer(wsv)(v => println(v.getOrElse("cancelling...").toString()))
  val wprod = new WaitingProducer(wsv)

  wprod.produce(List.range(0, 15))
  wcons.consume()
}


class SyncVar[T] {

  var value: T = _
  @volatile var cancel = false
  @volatile var set = false

  private val syncLock = new AnyRef

  def getSyncLock(): AnyRef = syncLock

  def get(): T = {
    syncLock.synchronized {
      if (set) {
        set = false
        syncLock.notifyAll()
        value
      } else
        throw new Exception
    }
  }

  def put(x: T): Unit = {
    syncLock.synchronized {
      if (!set) {
        value = x
        set = true
        syncLock.notifyAll()
      } else
        throw new Exception
    }
  }

  def isEmpty(): Boolean = {
    syncLock.synchronized {
      !set
    }
  }

  def nonEmpty(): Boolean = {
    set
  }

  def getWait(): Option[T] = {
    var res:Option[T] = None
    syncLock.synchronized {
      breakable {
        while (!set) {
          if (!cancel) {
            syncLock.wait()
          } else {
            break()
          }
        }
      }
      if (cancel) {
        None
      } else {
        set = false
        syncLock.notifyAll()
        Some(value)
      }
    }
  }

  def putWait(v: T) = {
    syncLock.synchronized {
      while (set) {
        syncLock.wait()
      }
      value = v
      set = true
      syncLock.notifyAll()
    }
  }
}

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

class WaitingProducer[T](v: SyncVar[T]) {

  def produce(range: List[T]): Unit = {
    new Thread(() => {
      range.foreach(r => {
        v.putWait(r)
        Thread.sleep(100)
      })
      v.cancel = true
      v.getSyncLock().synchronized {
        v.getSyncLock().notifyAll()
      }
    }).start()
  }
}

class WaitingConsumer[T](v: SyncVar[T])(f:Option[T] => Unit) {
  def consume(): Unit = {
    new Thread(() => {
      while (!v.cancel) {
        f(v.getWait())
      }
    }).start()
  }
}

