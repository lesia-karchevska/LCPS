package chapter5.exercise8

import scala.util.Random

object HeapTester extends App {

  println("Elements inserted: ")
  var h = new HeapStruct[Int]
  val rnd: Random = new Random
  var x: Int = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  print(x + "; ")
  x = rnd.nextInt(30)
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
  val min1 = h.min

  println("Heap after another min: ")
  h.printHeap
}
