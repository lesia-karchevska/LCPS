package chapter5.exercise5

object QuadTreeFillExerciser extends App {

  val topLeft = new Point(0.0, 0.0)
  val bottomRight = new Point(10.0, 10.0)
  val body1 = new BodyImpl(new Point(1.3, 2.5), 23)
  val body2 = new BodyImpl(new Point(5.1, 7.9), 23)
  val body3 = new BodyImpl(new Point(0.4, 1.35), 23)
  val body4 = new BodyImpl(new Point(2.17, 3.5), 23)
  val r = QuadNode.getQuadTree(List(body1, body2, body3, body4), topLeft, bottomRight)
}
