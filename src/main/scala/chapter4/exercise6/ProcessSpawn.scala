package chapter4.exercise6

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, blocking}
import scala.sys.process._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object ProcessSpawn extends App {

  def spawnWithGlobal(command: String): Future[Int] = {
    Future {
      blocking {
        command.!
      }
    }
  }

  def spawn(command: String): Future[Int] = {

    val ec = ExecutionContext.fromExecutor(
      new java.util.concurrent.ForkJoinPool(1)
    )
    Future.apply({
      command.!
    })(ec)
  }

  println("Please enter path to jar to launch: ")
  val path = scala.io.StdIn.readLine()
  val f = spawn("java -jar "  + path)
  Await.result(f, Duration.Inf)
  f.value.get match {
    case Success(value) => println("exit code: " + value)
    case Failure(exception) => println("exception: " + exception)
  }

  Range(0, 50).foreach(i => {
    val fG = spawnWithGlobal(path)
    Await.result(fG, Duration.Inf)
    fG.value.get match {
      case Success(value) => println("exit code: " + value)
      case Failure(exception) => println("exception: " + exception)
    }
  })

}
