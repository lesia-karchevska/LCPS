package chapter2.exercises8_10

import scala.collection.mutable

//this is a slightly modified SyncQueue form exercises 3 - 6
class PrioritySyncQueue(capacity: Int){

  def this() = {
    this(0)
  }

  private val bounded: Boolean = capacity > 0
  private val lock = new AnyRef
  private val q = new mutable.PriorityQueue[PriorityTask]
  @volatile var cancel: Boolean = false

  def getSyncLock(): AnyRef = {
    lock
  }

  def remove(priority: Int): Unit = {
    lock.synchronized {
      val remaining = q.filter(t => t.priority >= priority)
      q.dequeueAll
      remaining.foreach(t => q.enqueue(t))
    }
  }

  def get(): PriorityTask = {
    lock.synchronized {
      if (q.nonEmpty) {
        lock.notifyAll()
        q.dequeue()
      } else {
        throw new Exception
      }
    }
  }

  def put(v: PriorityTask) = {
    lock.synchronized {
      if (!bounded || q.size < capacity) {
        q.enqueue(v)
        lock.notifyAll()
      } else {
        throw new Exception
      }
    }
  }

  def isFull(): Boolean = {
    lock.synchronized {
      !(bounded && q.size < capacity)
    }
  }

  def isEmpty(): Boolean = {
    lock.synchronized {
      q.isEmpty
    }
  }

  def getWait(): Option[PriorityTask]= {
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

  def putWait(v: PriorityTask) = {
    lock.synchronized {
      while (bounded && q.size == capacity) lock.wait()
      if (!cancel) q.enqueue(v)
      lock.notifyAll()
    }
  }

  def putWait(values: Seq[PriorityTask]) = {
    lock.synchronized {
      while (bounded && q.size == capacity) lock.wait()
      if (!cancel) values.foreach(v => q.enqueue(v))
      lock.notifyAll()
    }
  }
}
