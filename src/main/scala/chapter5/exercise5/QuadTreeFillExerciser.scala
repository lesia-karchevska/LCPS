package chapter5.exercise5

import chapter5.Timed

object QuadTreeFillExerciser extends App {

  val topLeft = new Point(0.0, 0.0)
  val bottomRight = new Point(10.0, 10.0)
  val body1 = BodyImpl(new Point(1.3, 2.5), 23)
  val body2 = BodyImpl(new Point(5.1, 7.9), 23)
  val body3 = BodyImpl(new Point(0.4, 1.35), 23)
  val body4 = BodyImpl(new Point(2.17, 3.5), 23)
  val r = QuadNode.getQuadTree(List(body1, body2, body3, body4), topLeft, bottomRight)

  println(Timed.timed { QuadNode.getCenterAndMass(r) })

}
