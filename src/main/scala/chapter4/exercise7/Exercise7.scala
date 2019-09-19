package chapter4.exercise7

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Success

object Exercise7 extends App {

  val map = new IMap[Int, Int]
  val random = new scala.util.Random()
  @volatile var go_on = true

  List.range(0, 5).foreach(i => {
    new Thread(){
      override def run() = {
        this.setName("thread-" + i)
        while (go_on) {
          val random = new scala.util.Random()
          val action = random.nextInt(2)
          val sleep = random.nextInt(200)
          Thread.sleep(sleep)
          action % 2 match {
            case 0 => {
              val key = random.nextInt(50)
              val value = random.nextInt(30)
              try {
                map.update(key, 1)
                println("thread-" + i + " updated key " + key +  " with value "  + value)
              } catch {
                case _ => println("thread-" + i + " failed to update key " + key +  " with value "  + value)
              }
            }
            case _ =>  {
              val key = random.nextInt(50)
              val keyFuture = map.apply(key)
              try {
                Await.result(keyFuture, Duration.fromNanos(500000000))
                println("thread-" + i + " got value " + (keyFuture.value.get match { case Success(value) => value }) + " for key " + key)
              } catch {
                case _ => println("thread-" + i + " got no value for key " + key + " after waiting for 500 milliseconds")
              }
            }
          }
        }
      }
    }.start()
  })

  Thread.sleep(10000)
  go_on = false
}
