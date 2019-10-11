package chapter5.exercise5

import scala.annotation.tailrec

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

class Point(val x: Double, val y: Double)

abstract class Body
case class BodyImpl(coordinates: Point, mass: Double) extends Body
case object BodyNil extends Body

object QuadNode {

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
}
