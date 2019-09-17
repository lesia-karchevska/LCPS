package chapter4.exercise2

object Exercise2 extends App {
  val ivar = new IVar[Int]
  val random = new scala.util.Random()
  List.range(0, 7).foreach(i => {
    new Thread(){
      override def run() = {
        this.setName("thread-" + i)
        val random = new scala.util.Random()
        val action = random.nextInt(2)
        val sleep = random.nextInt(200)
        Thread.sleep(sleep)
        action % 2 match {
          case 0 => {
            val value = random.nextInt(100)
            try {
              ivar := value
              println("thread-" + i + " assigned value " + value)
            } catch {
              case _ => println("thread-" + i + " failed to assign value " + value + " to ivar")
            }
          }
          case _ =>  {
            try {
              println("thread-" + i + " read value " + ivar.apply())
            } catch {
              case _ => println("thread-" + i + " got no value from ivar")
            }
          }
        }
      }
    }.start()
  })
}
