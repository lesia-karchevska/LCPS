package chapter2.exercises3_6

import scala.util.control.Breaks.{break, breakable}

class SyncVar[T] extends Sync[T] {

  @volatile var cancel: Boolean = false
  var value: T = _
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
