package chapter5.exercise8

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class HeapStruct[T](implicit ord: Ordering[T]) extends Iterable[T]{

  var heapHead: Option[Node[T]] = None

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

    go(None, heapHead)
  }

  def printHeapRow(start: Node[T]): ArrayBuffer[Node[T]] = {
    val children: ArrayBuffer[Node[T]] = new ArrayBuffer[Node[T]]
    def go(node: Node[T]): Unit = {
      System.out.print(node.key.get + "; ")
      node.child match {
        case None =>
        case Some(ch) => children.addOne(ch)
      }
      node.sibling match {
        case None =>
        case Some(next) =>
          go(next)
      }
    }
    go(start)
    children
  }

  def printHeap(): Unit = {
    def go(elements: ArrayBuffer[Node[T]]): Unit = {
      if (elements.size > 0) {
        val newChildren = new ArrayBuffer[Node[T]]
        elements.foreach(el => newChildren.addAll(printHeapRow(el)))
        println
        go(newChildren)
      }
    }

    heapHead match {
      case None =>
      case Some(h) =>
        val initial = new ArrayBuffer[Node[T]]
        initial.addOne(h)
        go(initial)
    }

  }

  override def iterator: Iterator[T] = new BinomialIterator()

  private class BinomialIterator extends Iterator[T] {

    def initHasNextElement(): Boolean = {
      heapHead match {
        case None =>
          false
        case Some(x) =>
          nextStack.push(x)
          true
      }
    }

    private val nextStack: mutable.Stack[Node[T]] = new mutable.Stack[Node[T]]()
    private var hasNextElement: Boolean = initHasNextElement()

    def BinomialIterator() {
      heapHead match {
        case None =>
          hasNextElement = false
        case Some(x) =>
          hasNextElement = true
          nextStack.push(x)
      }
    }

    private def putToStackIfPresent(node: Option[Node[T]]) = {
      node match {
        case None =>
        case Some(x) =>
          nextStack.push(x)
      }
    }

    override def hasNext: Boolean = {
      hasNextElement
    }

    override def next(): T = {
      if (!hasNextElement) throw new IndexOutOfBoundsException
      else {
        val elem = nextStack.pop()
        putToStackIfPresent(elem.child)
        putToStackIfPresent(elem.sibling)
        hasNextElement = !nextStack.isEmpty
        elem.key.get
      }
    }
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

    var newHead = merge(h1.heapHead, h2.heapHead)

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
              go(prev, nextElem, nextElem.sibling)
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
          heapHead = newHead
        }
    }
  }

  def insert[T](insKey: T, heap: HeapStruct[T])(implicit ord: Ordering[T]): HeapStruct[T] = {
    val keyHeap = new HeapStruct[T] {
      heapHead = Some(new Node[T] {
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
    curr.sibling = prev
    next match {
      case None =>
        curr
      case Some(nx) =>
        val newNext = nx.sibling
        reverse(Some(curr), nx, newNext)
    }
  }

  private def getReverseStruct[T](oldHead: Option[Node[T]])(implicit ord: Ordering[T]): HeapStruct[T] = {
    oldHead match {
      case None => new HeapStruct[T] { heapHead = None }
      case Some(oh) =>
        new HeapStruct[T]() {
          heapHead = Some(reverse(None, oh, oh.sibling))
        }
    }
  }

  private def doActionOnChildren[T](childStart: Option[Node[T]], action: Node[T] => Unit): Unit = {
    def go(n: Option[Node[T]]): Unit = {
      n match {
        case None =>
        case Some(x) =>
          action.apply(x)
          go(x.sibling)
      }
    }
    go(childStart)
  }

  def extractMin[T](heap: HeapStruct[T])(implicit ord: Ordering[T]): Option[T] = {
    heap.heapHead match {
      case Some(node) =>
        @tailrec
        def go(minPrev: Option[Node[T]], min: Node[T], currPrev: Option[Node[T]], curr: Option[Node[T]]): (Node[T], Option[Node[T]]) = {
          curr match {
            case Some(x) =>
              if (ord.compare(min.key.get, x.key.get) < 0) go(minPrev, min, curr, x.sibling)
              else go(currPrev, x, curr, x.sibling)
            case None =>
              (min, minPrev)
          }
        }

        val minNodeInfo = go(None, node, heap.heapHead, node.sibling)
        //removing minimum element from the list of roots:
        minNodeInfo match {
          case (min, None) => heap.heapHead = min.sibling
          case (min, Some(prev)) => prev.sibling = min.sibling
        }
        doActionOnChildren[T](minNodeInfo._1.child, ch => { ch.parent = None })
        //get heap which root list is the reversed list of children of the minimal key
        val minHeap = getReverseStruct(minNodeInfo._1.child)
        val newHeap = union(heap, minHeap)
        heap.heapHead = newHeap.heapHead
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
            if (ord.compare(y.key.get, p.key.get) < 0) {
              val temp = y.key
              y.key = p.key
              p.key = temp
              go(p, p.parent)
            }
        }
      }

      go(node, node.parent)
    }
  }

}
