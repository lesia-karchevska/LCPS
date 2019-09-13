package chapter3.exercise9

import java.util.concurrent.atomic.AtomicReference

import scala.annotation.tailrec

class LockFreeSyncPool[T] {
  class Node[T] {
    var value: Option[T] = None
    var next: AtomicReference[(Boolean, Node[T])] = new AtomicReference[(Boolean, Node[T])]((true, null))
  }

  val parallelism = 5
  val buckets =
    new Array[AtomicReference[(Boolean, Node[T])]](parallelism)
  for (i <- 0 until buckets.length)
    buckets(i) = new AtomicReference((true, null))
  val heads = new Array[AtomicReference[(Long, AtomicReference[(Boolean, Node[T])])]](parallelism)
  for (i <- 0 until buckets.length)
    heads(i) = new AtomicReference((0L, buckets(i)))

  def add(x: T): Unit = {
    val i =
      ((Thread.currentThread.getId ^ x.##) % buckets.length).toInt
    @tailrec def retry() {
      val head: AtomicReference[(Long, AtomicReference[(Boolean, Node[T])])]  = heads(i)
      val currentRef = head.get
      val (stamp, nodeRef) = currentRef
      val currentNode = nodeRef.get
      val (valid, node) = currentNode
      val nextStamp = stamp + 1
      if (!valid) {
        val nextRef: AtomicReference[(Boolean, Node[T])] = node.next
        head.compareAndSet(currentRef, (nextStamp, nextRef))
        retry()
      } else {
        val newNode = new Node[T]
        newNode.next = nodeRef
        newNode.value = Some(x)
        val newRef = new AtomicReference[(Boolean, Node[T])]((valid, newNode))
        if (!head.compareAndSet(currentRef, (nextStamp, newRef))) retry()
      }
    }
    retry()
  }

  def remove(): Option[T] = {
    val start =
      (Thread.currentThread.getId % buckets.length).toInt
    @tailrec def scan(witness: Long): Option[T] = {
      var i = (start + 1) % buckets.length
      var sum = 0L
      while (i != start) {
        val head = heads(i)
        @tailrec def retry(): Option[T] = {
          val current = head.get
          val (stamp, nodeRef) = current
          val currentElem = nodeRef.get
          val (valid, node) = currentElem
          if (node == null) {
            sum += stamp
            None
          } else {
            if (!valid) {
              val nextRef: AtomicReference[(Boolean, Node[T])] = node.next
              head.compareAndSet(current, (stamp + 1, nextRef))
              retry()
            } else {
              if (nodeRef.compareAndSet(currentElem, (false, node))) {
                node.value
              } else retry()
            }
          }
        }
        retry() match {
          case Some(v) => return Some(v)
          case None =>
        }
        i = (i + 1) % heads.length
      }
      if (sum == witness) None
      else scan(sum)
    }
    scan(-1L)
  }

  def foreach(f: T => Unit) = {
    heads.foreach(head => {
      val (_, bucket) = head.get
      iterate(bucket, f)
    })
  }

  private def iterate(bucket: AtomicReference[(Boolean, Node[T])], func: T => Unit): Unit = {
    bucket.get match {
      case (_, null) => {
        println("empty bucket")
      }
      case (true, node) => {
        //at this moment some other thread might have already removed the element from the pool
        //if we don't want this to happen, we could try to introduce another state - say, 'pending',
        //remove the element from the list while func is being executed and return it to the list when it is done,
        //modifying at the same time the count of pending elements
        //if this is possible to be done, other threads won't be able to remove elements possessed by iterator
        node.value match {
          case Some(x) =>
            func(x)
            iterate(node.next, func)
        }
      }
      case (false, node) => iterate(node.next, func)
    }
  }
}
