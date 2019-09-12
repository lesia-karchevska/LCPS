package chapter3.exercise3

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec

class ConcurrentSortedList_v2[T](implicit ord: Ordering[T]) {

  class Node[T] {
    def this(x: T, next: AtomicReference[Option[Node[T]]]) = {
      this()
      this.x = x
      this.next = next
    }
    var x: T = _
    var next: AtomicReference[Option[Node[T]]] = new AtomicReference(None)
  }

  private val head: AtomicReference[Option[Node[T]]] = new AtomicReference(None)

  def add(x: T): Unit = {
    val current = head.get
    current match {
      case None => //empty list case
        val node = new Node[T](x, new AtomicReference(None))
        if (!head.compareAndSet(current, Some(node))) add(x)
      case _ => //nonempty list case
        add(x, head)
    }
  }

  @tailrec private def add(x: T, nodeRef: AtomicReference[Option[Node[T]]]): Unit = {
    val nodeVal = nodeRef.get
    nodeVal match {
      case None =>
        val node = new Node[T](x, new AtomicReference(None))
        if (!nodeRef.compareAndSet(nodeVal, Some(node))) add(x, nodeRef)
      case Some(currNode) =>
        if(ord.compare(x, currNode.x) > 0) {
          //these are the only elements we are creating in case there are no concurrent add invocations
          val nextNode = new Node(currNode.x, currNode.next)
          val nodeOption: Option[Node[T]] = Some(nextNode)
          val newNode = new Node(x, new AtomicReference(nodeOption))
          if(!nodeRef.compareAndSet(nodeVal, Some(newNode))) add(x, nodeRef)
        } else add(x, currNode.next)
    }
  }

  def iterator: Iterator[T] = new ConcurrentIterator

  //this iterator will see certain (not all) concurrent adds that happened downstream its current position
  // upstream concurrent adds will be invisible
  class ConcurrentIterator extends Iterator[T] {

    private var currentPos: Option[Node[T]] = head.get

    override def hasNext: Boolean = {
      currentPos match {
        case None => false
        case Some(_) => true
      }
    }

    override def next(): T = {
      currentPos match {
        case Some(x) =>
          val value = x.x
          currentPos = x.next.get
          value
      }
    }
  }

}
