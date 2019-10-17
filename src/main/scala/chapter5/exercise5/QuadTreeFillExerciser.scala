package chapter5.exercise5

import chapter5.Timed

import scala.util.Random

object QuadTreeFillExerciser extends App {


  def createRandomBody(maxX: Double, maxY: Double, maxMass: Double): BodyImpl = {
    val rnd = new Random()
    BodyImpl(new Point(rnd.nextDouble() * maxX, rnd.nextDouble() * maxX), rnd.nextDouble() * maxMass)
  }

  val topLeft = new Point(0.0, 0.0)
  val bottomRight = new Point(10.0, 10.0)
  val body1 = BodyImpl(new Point(1.3, 2.5), 23)
  val body2 = BodyImpl(new Point(5.1, 7.9), 23)
  val body3 = BodyImpl(new Point(0.4, 1.35), 23)
  val body4 = BodyImpl(new Point(2.17, 3.5), 23)
  val r = QuadNode.getQuadTree(List(body1, body2, body3, body4), topLeft, bottomRight)

  //test results correctness
//  val testbody1 = BodyImpl(new Point(1, 1), 2)
//  val testbody2 = BodyImpl(new Point(4, 2), 3)
//  val testbody3 = BodyImpl(new Point(2, 5), 4)


 //val point = QuadNode.computeForcesSequential(List(testbody1, testbody2, testbody3), new Point(0,0), new Point(5, 6), 0.5).filter(elem => elem match  { case (p: Point, b: BodyImpl) =>{
//    b.mass < 3}
 // })

  //println("test force vector: (" + point(0)._1.x + ", " + point(0)._1.y + ")")

 // println(Timed.timed { QuadNode.getCenterAndMass(r) })

  println("enter number of random bodies:")
  val num = scala.io.StdIn.readInt()
  val bodies = Range(0, num).map(n => createRandomBody(10.0, 10.0, 200))
  println("parallel computation time: " + Timed.timed {QuadNode.computeForcesParallel(bodies, topLeft, bottomRight, 0.8)})
  println("sequential computation time: " + Timed.timed {QuadNode.computeForcesSequential(bodies, topLeft, bottomRight, 0.8)})
}
