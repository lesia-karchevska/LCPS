package chapter5.exercise5

import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters._

abstract class QuadNode
case class QuadTree(topLeft: Point, bottomRight: Point, parent: QuadNode) extends QuadNode {
  var center: Point = new Point(0.0, 0.0)
  var mass: Double = 0.0
  var body: Body = BodyNil
  var nw: QuadNode = QuadNil
  var ne: QuadNode = QuadNil
  var sw: QuadNode = QuadNil
  var se: QuadNode = QuadNil
}
case object QuadNil extends QuadNode

class Point(val x: Double, val y: Double) {

   def +(other: Point): Point = {
     new Point(this.x + other.x, this.y + other.y)
   }
}

abstract class Body
case class BodyImpl(coordinates: Point, mass: Double, force: Double = 0) extends Body
case object BodyNil extends Body

object QuadNode {

  val G = 6.67 * Math.pow(10, -11)

  def getQuadTree(bodies: Seq[BodyImpl], topLeft: Point, bottomRight: Point): QuadNode = {
    val root = QuadTree(topLeft, bottomRight, QuadNil)
    bodies.foreach(body => allocateBody(body, root))
    root
  }

  def getCenter(node: QuadTree): Point = {
    new Point((node.topLeft.x + node.bottomRight.x) / 2.0, (node.topLeft.y + node.bottomRight.y) / 2.0)
  }

  def isLeaf(node: QuadTree): Boolean = {
    node.nw.equals(QuadNil) && node.ne.equals(QuadNil) && node.sw.equals(QuadNil) && node.se.equals(QuadNil)
  }

  def createNW(node: QuadTree): QuadTree = {
    val child = QuadTree(node.topLeft, getCenter(node), node)
    node.nw = child
    child
  }

  def createNE(node: QuadTree): QuadTree = {
    val child = QuadTree(new Point((node.topLeft.x + node.bottomRight.x) / 2.0, node.topLeft.y), new Point(node.bottomRight.x, (node.topLeft.y + node.bottomRight.y) / 2.0), node)
    node.ne = child
    child
  }

  def createSE(node: QuadTree): QuadTree = {
    val child = QuadTree(getCenter(node), node.bottomRight, node)
    node.se = child
    child
  }

  def createSW(node: QuadTree): QuadTree = {
    val child = QuadTree(new Point(node.topLeft.x, (node.topLeft.y  + node.bottomRight.y) / 2.0), new Point((node.topLeft.x + node.bottomRight.x) / 2.0, node.bottomRight.y), node)
    node.sw = child
    child
  }

  def getNextQuad(body: BodyImpl, currQuad: QuadTree): QuadTree = {
    val cX = (currQuad.topLeft.x + currQuad.bottomRight.x) / 2.0
    val cY = (currQuad.topLeft.y + currQuad.bottomRight.y) / 2.0
    if (body.coordinates.x < cX) {
      if (body.coordinates.y < cY) {
        currQuad.nw match {
          case QuadNil => createNW(currQuad)
          case t: QuadTree => t
        }
      }
      else currQuad.sw match {
        case QuadNil => createSW(currQuad)
        case t: QuadTree => t
      }
    } else {
      if (body.coordinates.y < cY) {
        currQuad.ne match {
          case QuadNil => createNE(currQuad)
          case t: QuadTree => t
        }
      }
      else {
        currQuad.se match {
          case QuadNil => createSE(currQuad)
          case t: QuadTree => t
        }
      }
    }
  }

  @tailrec
  def allocateBody(body: BodyImpl, node: QuadTree): Unit = {
    if (isLeaf(node)) {
      val movedBody = node.body
      movedBody match {
        case b: BodyImpl =>
          val next = getNextQuad(b, node)
          next.body = b
          node.body = BodyNil
          allocateBody(body, getNextQuad(body, node))
        case BodyNil =>
          node.body = body
      }
    } else {
      allocateBody(body, getNextQuad(body, node))
    }
  }

  def getCenterAndMass(node: QuadNode): BodyImpl = {
    node match {
      case QuadNil => BodyImpl(new Point(0, 0), 0)
      case t: QuadTree => {
        t.body match {
          case BodyNil => {
            val nw = getCenterAndMass(t.nw)
            val ne = getCenterAndMass(t.ne)
            val sw = getCenterAndMass(t.sw)
            val se = getCenterAndMass(t.se)
            t.mass = nw.mass + ne.mass + sw.mass + se.mass
            t.center = new Point((nw.coordinates.x * nw.mass + ne.coordinates.x * ne.mass + sw.coordinates.x * sw.mass + se.coordinates.x * se.mass) / (nw.mass + ne.mass + sw.mass + se.mass),
              (nw.coordinates.y * nw.mass + ne.coordinates.y * ne.mass + sw.coordinates.y * sw.mass + se.coordinates.y * se.mass) / (nw.mass + ne.mass + sw.mass + se.mass))
            BodyImpl(t.center, t.mass)
          }
          case b: BodyImpl => b
        }
      }
    }

  }

  private def getDistance(a: Point, b: Point): Double = {
    Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y))
  }
  private def getGravityForceVector(p: Point, pOther: Point, m: Double, mOther: Double, distance: Double): Point = {
    val force =  m * mOther / Math.pow(distance, 2)
    new Point((pOther.x - p.x) * force / distance, (pOther.y - p.y) * force / distance)
  }

  @tailrec
  private def go(body: BodyImpl, force: Point, nodesToTraverse: Seq[QuadNode], theta: Double):  Point = {
    nodesToTraverse match {
      case Seq() => force
      case _ =>
        var newNodesToTraverse = Seq[QuadNode]()
        var newForce = force
        nodesToTraverse.foreach(node => {
          node match {
            case n: QuadTree =>
              n.body match {
                case BodyNil =>
                  val distance = getDistance(body.coordinates, n.center)
                  if ((n.bottomRight.x - n.topLeft.x) / distance < theta){
                    newForce = newForce + getGravityForceVector(body.coordinates, n.center, body.mass, n.mass, distance)
                  } else {
                    newNodesToTraverse = newNodesToTraverse :+ n.ne
                    newNodesToTraverse = newNodesToTraverse :+ n.nw
                    newNodesToTraverse = newNodesToTraverse :+ n.se
                    newNodesToTraverse = newNodesToTraverse :+ n.sw
                  }
                case b: BodyImpl =>
                  val d = getDistance(b.coordinates, body.coordinates)
                  if (d > 0) newForce =  newForce + getGravityForceVector(body.coordinates, b.coordinates, body.mass, b.mass, d)
              }
            case QuadNil =>
          }
        })
        go(body, newForce, newNodesToTraverse, theta)
    }
  }

  //computes forces acting on bodies in parallel
  def computeForcesParallel(bodies: Seq[BodyImpl], topLeft: Point, bottomRight: Point, theta: Double): Seq[(Point, BodyImpl)] = {

    val root = getQuadTree(bodies, topLeft, bottomRight)
    getCenterAndMass(root)
    bodies.par.map(b => (go(b, new Point(0, 0), Seq(root), theta), b)).toList
  }

  def computeForcesSequential(bodies: Seq[BodyImpl], topLeft: Point, bottomRight: Point, theta: Double): Seq[(Point, BodyImpl)] = {

    val root = getQuadTree(bodies, topLeft, bottomRight)
    getCenterAndMass(root)
    bodies.map(b => (go(b, new Point(0, 0), Seq(root), theta), b))
  }
}
