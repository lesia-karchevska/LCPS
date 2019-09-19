package chapter4.exercise8

import scala.concurrent.Promise
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object Exercise8 extends App {

  //happy path
  val originalSuccess = Promise[Int]
  val resultingSuccess = new PromiseCompose(originalSuccess)

  println("example: happy path")
  println("Input any string: ")
  val string = scala.io.StdIn.readLine()
  originalSuccess.future.onComplete({
    case Success(value) => println("original: the length of the string is: " + value)
    case Failure(_) => println("original: something went wrong!")
  })
  resultingSuccess.compose[String](x => x.length) success string

  Thread.sleep(1000)

  //fail the resulting promise
  val originalFail = Promise[Int]
  val resultingFail = new PromiseCompose(originalFail)

  println("example: fail the resulting promise")
  originalFail.future.onComplete({
    case Success(value) => println("original: the length of the string is: " + value)
    case Failure(_) => println("original: something went wrong!")
  })
  resultingFail.compose[String](x => x.length) failure new Exception


  Thread.sleep(1000)

  //original promise has already completed
  val originalCompleted = Promise[Int]
  val resultingCompleted = new PromiseCompose(originalCompleted)

  println("example: original already completed")
  originalCompleted success -1
  resultingCompleted.compose[String](x => x.length) success "whatever..."
  originalCompleted.future.onComplete({
    case Success(value) => println("original: the length of the string is: " + value)
    case Failure(_) => println("original: something went wrong!")
  })
}
