package chapter3.exercise3

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec

class ConcurrentSortedList[T] (implicit val ord: Ordering[T]){

  //first version of the sorted list
  class SortedLinkedList[T]

  val parallelism = Runtime.getRuntime.availableProcessors
  val buckets = new Array[AtomicReference[List[T]]](parallelism)

  for(i <- 0 until buckets.length)
    buckets(i) = new AtomicReference[List[T]](Nil)

  def add(x: T): Unit = {
    val i = ((Thread.currentThread.getId ^ x.##) % buckets.length).toInt
    @tailrec def retry(): Unit = {
      val bucket = buckets(i)
      val b = bucket.get()
      val newBucket = put(x, b)
      if (!bucket.compareAndSet(b, newBucket)) retry()
    }
    retry()
  }

  private def put(x: T, bucket: List[T]): List[T] = {
    bucket match {
      case Nil => x::Nil
      case h::tail => {
        if (ord.compare(x, h) > 0) {
          x::bucket
        } else {
          h::put(x, tail)
        }
      }
    }
  }

  def iterator: Iterator[T] = new ConcurrentSortedListIterator

  //this iterator will only traverse the list correctly in case there were no calls to add method of the list (even from the same thread)
  //on each step partially merges the parts of the list that are already sorted
  class ConcurrentSortedListIterator extends Iterator[T] {
    private var lists:Array[List[T]] = buckets.map(ar => ar.get())
    private var nextElem: Option[T] = None

    private def getNext(): Option[T] = {
      this.synchronized {
        val nonempty = lists.filter(li => li.nonEmpty)

        if (nonempty.nonEmpty) {
          var newLists:List[List[T]] = Nil
          val firstList = nonempty.head
          val firstVal = firstList(0)
          val (v, h::tail) = nonempty.tail.foldRight((firstVal, firstList))((nextL, curr) => {
            (nextL, curr) match {
              case (nx::_, (cv, cl)) => {
                if (ord.compare(nx, cv) > 0) {
                  newLists = cl::newLists
                  (nx, nextL)
                } else {
                  newLists = nextL::newLists
                  curr
                }
              }
            }
          })
          lists = (tail::newLists).toArray
          Some(v)
        } else {
          None
        }
      }
    }

    def hasNext():Boolean = {
      nextElem = getNext()
      nextElem match {
        case Some(_) => true
        case None => false
      }
    }

    def next(): T= {
      nextElem match {
        case Some(x) => x
      }
    }
  }
}
