package chapter5.exercise3

import chapter5.Timed
import scalafx.application.JFXApp
import scalafx.scene.image.{ImageView, WritableImage}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color
import scalafx.scene.{Group, Scene}

import scala.collection.parallel.CollectionConverters._

object exercise3 extends JFXApp{
  val max_iteration = 500
  val a = 500
  val max_width = 3 * a
  val max_height = 2 * a

  def getPoints(maxX: Int, maxY: Int): Seq[(Int, Int)] = for {
    x <- 0 until maxX
    y <- 0 until maxY
  } yield (x, y)

  def parallelRenderMandelbrotSet(): Seq[(Int, Int, Int)] = {
    getPoints(max_width, max_height).par.map({
      case (x, y) => {
        val m = Mandelbrot.getIteration(3 * (x - 2 * a).toDouble / max_width.toDouble, 2 * (y - a).toDouble / max_height.toDouble, max_iteration)
        (x, y, m)
      }
    }).toList
  }

  def renderMandelbrotSet(): Seq[(Int, Int, Int)] = {
    getPoints(max_width, max_height).map({
      case (x, y) => {
        val m = Mandelbrot.getIteration((x - 2 * a).toDouble / max_width.toDouble, (y - a).toDouble / max_height.toDouble, max_iteration)
        (x, y, m)
      }
    }).toList
  }

  stage = new JFXApp.PrimaryStage {
    height = max_height
    width = max_width
  }

  val image = new WritableImage(max_width, max_height)
  val pw = image.getPixelWriter

  var set = Seq[(Int, Int, Int)]()
  println("par: " + Timed.timed { set = parallelRenderMandelbrotSet })
  println("seq: " + Timed.timed { renderMandelbrotSet })

  set.foreach( {
    case (x, y, m) => {
      pw.setColor(x, y, Color.hsb(m.toDouble / max_iteration, m.toDouble / max_iteration, Math.signum(max_iteration - m)))
    }
  })

  val scene = new Scene(new Group)
  val root = new VBox
  val imageView = new ImageView
  imageView.setImage(image)
  root.getChildren.add(imageView)
  scene.setRoot(root)
  stage.scene = scene
  stage.show()
}
