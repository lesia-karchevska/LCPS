package chapter2.exercises3_6

import scala.collection.mutable

class SyncQueue[T] extends Sync[T]{

  private var n: Int = 0
  private val lock = new AnyRef
  private val q = new mutable.Queue[T]
  @volatile var cancel: Boolean = false

  def getSyncLock(): AnyRef = {
    lock
  }

  def setCapacity(c: Int) = {
    lock.synchronized {
      if (n < c) {
        n = c
      }
    }
  }

  def get(): T = {
    lock.synchronized {
      if (q.nonEmpty) {
        lock.notifyAll()
        q.dequeue()
      } else {
        throw new Exception
      }
    }
  }

  def put(v: T) = {
    lock.synchronized {
      if (q.size < n) {
        q.enqueue(v)
        lock.notifyAll()
      } else {
        throw new Exception
      }
    }
  }

  def isFull(): Boolean = {
    lock.synchronized {
      q.size == n
    }
  }

  def getWait(): Option[T]= {
    lock.synchronized {
      while (q.isEmpty && !cancel) lock.wait()
      lock.notifyAll()
      if (!cancel) {
        Some(q.dequeue())
      } else {
        None
      }
    }
  }

  def putWait(v: T) = {
    lock.synchronized {
      while (q.size == n) lock.wait()
      q.enqueue(v)
      lock.notifyAll()
    }
  }
}
