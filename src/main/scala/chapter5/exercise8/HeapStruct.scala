package chapter5.exercise8

import scala.annotation.tailrec

class Node[T] {
  var key: Option[T] = None
  var parent: Option[Node[T]] = None
  var sibling: Option[Node[T]] = None
  var child: Option[Node[T]] = None
  var degree: Int = 0
}

class HeapStruct[T](implicit ord: Ordering[T]) {
  var head: Option[Node[T]] = None

  def min: Option[T] = {
    @tailrec
    def go(curr: Option[T], node: Option[Node[T]]): Option[T] = {
      node match {
        case None => curr
        case Some(n) => {
          curr match {
            case None =>
              go(Some(n.key.get), n.sibling)
            case Some(x) =>
              if (ord.compare(x, n.key.get) > 0) go(Some(n.key.get), n.sibling)
              else go(curr, n.sibling)
          }
        }
      }
    }

    go(None, head)
  }

}

object HeapStruct {

  def binomialLink[T](x: Node[T], y: Node[T]): Node[T] = {
    x.sibling = y.child
    y.child = Some(x)
    y.degree = y.degree + 1
    x.parent = Some(y)
    y
  }

  def union[T](h1: HeapStruct[T], h2: HeapStruct[T])(implicit ord: Ordering[T]): HeapStruct[T] = {
    def merge(hl1: Option[Node[T]], hl2: Option[Node[T]]): Option[Node[T]] = {
      @tailrec
      def go(hl1: Option[Node[T]], hl2: Option[Node[T]], curr: Node[T], head: Node[T]): Node[T] = {
        (hl1, hl2) match {
          case (Some(n1), Some(n2)) =>
            if (n1.degree > n2.degree) {
              curr.sibling = hl2
              go(hl1, n2.sibling, n2, head)
            } else {
              curr.sibling = hl1
              go(n1.sibling, hl2, n1, head)
            }
          case (None, Some(n2)) =>
            curr.sibling = hl2
            head
          case (Some(n1), None) =>
            curr.sibling = hl1
            head
          case _ =>
            curr.sibling = None
            head
        }
      }

      (hl1, hl2) match {
        case (Some(n1), Some(n2)) => {
          if (n1.degree > n2.degree) Some(go(hl1, n2.sibling, n2, n2))
          else Some(go(n1.sibling, hl2, n1, n1))
        }
        case (None, Some(_)) => hl2
        case (Some(_), None) => hl1
        case _ => None
      }
    }

    var newHead = merge(h1.head, h2.head)

    @tailrec
    def go(prev: Option[Node[T]], x: Node[T], next: Option[Node[T]]): Unit = {
      next match {
        case None =>
        case Some(nextElem) =>
          if ((x.degree != nextElem.degree) || (!nextElem.sibling.equals(None) && nextElem.sibling.get.degree == x.degree))
            go(Some(x), nextElem, nextElem.sibling)
          else {
            if (ord.compare(x.key.get, nextElem.key.get) <= 0) {
              x.sibling = nextElem.sibling
              binomialLink(nextElem, x)
              go(prev, x, x.sibling)
            } else {
              prev match {
                case None => newHead = next
                case Some(prevElem) => prevElem.sibling = next
              }
              binomialLink(x, nextElem)
              go(prev, nextElem, x.sibling)
            }
          }
      }

    }

    newHead match {
      case None => new HeapStruct[T]
      case Some(h) =>
        val x = h
        val next = x.sibling
        go(None, x, next)
        new HeapStruct {
          head = newHead
        }
    }
  }

  def insert[T](insKey: T, heap: HeapStruct[T])(implicit ord: Ordering[T]): HeapStruct[T] = {
    val keyHeap = new HeapStruct[T] {
      head = Some(new Node[T] {
        key = Some(insKey)
      })
    }
    union(heap, keyHeap)
  }

  private def last[T](node: Node[T]): Option[Node[T]] = {
    @tailrec
    def go(n: Node[T]): Node[T] = {
      n.sibling match {
        case None => n
        case Some(next) => go(next)
      }
    }

    Some(go(node))
  }

  @tailrec
  private def reverse[T](prev: Option[Node[T]], curr: Node[T], next: Option[Node[T]]): Node[T] = {
    next match {
      case None => curr
      case Some(nx) =>
        val newNext = nx.sibling
        curr.sibling = prev
        reverse(Some(curr), nx, newNext)
    }
  }

  private def getReverseStruct[T](oldHead: Option[Node[T]])(implicit ord: Ordering[T]): HeapStruct[T] = {
    oldHead match {
      case None => new HeapStruct[T] { head = None }
      case Some(oh) =>
        new HeapStruct[T]() {
          head = Some(reverse(None, oh, oh.sibling))
        }
    }
  }

  def extractMin[T](heap: HeapStruct[T])(implicit ord: Ordering[T]): Option[T] = {
    heap.head match {
      case Some(node) =>
        @tailrec
        def go(minPrev: Option[Node[T]], currMin: Node[T], currPrev: Option[Node[T]], next: Option[Node[T]]): (Node[T], Option[Node[T]]) = {
          next match {
            case Some(x) =>
              if (ord.compare(currMin.key.get, x.key.get) < 0) go(minPrev, currMin, next, x.sibling)
              else go(currPrev, x, next, x.sibling)
            case None =>
              (currMin, minPrev)
          }
        }

        val minNodeInfo = go(None, node, None, node.sibling)
        //removing minimum element from the list of roots:
        minNodeInfo match {
          case (min, None) => heap.head = min.sibling
          case (min, Some(prev)) => prev.sibling = min.sibling
        }
        //get heap which root list is the reversed list of children of the minimal key
        val minHeap = getReverseStruct(minNodeInfo._1.child)
        val newHeap = union(heap, minHeap)
        heap.head = newHeap.head
        minNodeInfo._1.key
      case None =>
        None
    }
  }

  def decreaseKey[T](h: HeapStruct[T], node: Node[T], k: T)(implicit ord: Ordering[T]): Unit = {
    if (ord.compare(node.key.get, k) > 0) {
      node.key = Some(k)

      @tailrec
      def go(y: Node[T], z: Option[Node[T]]): Unit = {
        z match {
          case None =>
          case Some(p) =>
            if (ord.compare(node.key.get, p.key.get) < 0) {
              val temp = node.key
              node.key = p.key
              p.key = temp
              go(p, p.parent)
            }
        }
      }

      go(node, node.parent)
    }
  }

}
