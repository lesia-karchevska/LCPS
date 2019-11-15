package chapter5.exercise8

import scala.util.Random

object HeapTester extends App {

  var h = new HeapStruct[Int]
  val rnd: Random = new Random
  var x: Int = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  println(x)
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  println(x)
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  println(x)
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  println(x)
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  println(x)
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  println(x)
  x = rnd.nextInt(30)
  h = HeapStruct.insert(x, h)
  println(x)
//  h = HeapStruct.insert(rnd.nextInt(20), h)
 // List.range(0, 50).foreach(_ => {
 //   h = HeapStruct.insert(rnd.nextInt, h)
 // })
  val min = h.min
  println(HeapStruct.extractMin(h).get)
  val min1 = h.min
}
