package chapter5.exercise8

import scala.util.Random

object HeapTester extends App {

  println("Elements inserted: ")
  var h = new HeapStruct[Int]
  val rnd: Random = new Random
  var x: Int = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(300)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(300)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(300)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(300)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(300)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(300)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(300)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(300)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  println
  println("heap built:")
  h.printHeap
//  h = HeapStruct.insert(rnd.nextInt(20), h)
 // List.range(0, 50).foreach(_ => {
 //   h = HeapStruct.insert(rnd.nextInt, h)
 // })
  val min = h.min

  println("Heap after min: ")
  h.printHeap

  println(HeapStruct.extractMin(h).get)

  println("Heap after extract min: ")
  h.printHeap
  var node = h.heapHead.get.child.get.child.get.child.get
  HeapStruct.decreaseKey(h, node, -1)

  println("Heap after decreasing key: ")
  h.printHeap()

  println("printing heap elements: ")
  val iter = h.iterator
  while (iter.hasNext) println(iter.next())
}
