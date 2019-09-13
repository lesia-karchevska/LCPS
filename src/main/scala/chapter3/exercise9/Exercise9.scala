package chapter3.exercise9

object Exercise9 extends App {

  val list = new LockFreeSyncPool[Int]()
  @volatile var terminate = false

  List.range(0, 5).foreach(i => {
    new Thread(){
      override def run() = {
        this.setName("adder-" + i)
        val random = new scala.util.Random()
        while (!terminate) {
          Thread.sleep(random.nextInt(50))
          val x = random.nextInt(15)
          list.add(x)
          println(this.getName + " added " + x + " to pool")
        }
      }
    }.start()
  })

  Thread.sleep(50)

  new Thread(){
    override def run() = {
      list.foreach(x => {
        println(" iterator thread got " + x.toString);
        Thread.sleep(8)
      })
    }
  }.start()

  List.range(0, 3).foreach(i => {
    new Thread(){
      override def run() = {
        this.setName("remover-" + i)
        val random = new scala.util.Random()
        while (!terminate) {
          Thread.sleep(random.nextInt(50))
          val elem = list.remove()
          elem match {
            case None => println(this.getName + " didn't find anything to remove")
            case Some(x) => println(this.getName + " removed " + x + " from the pool")
          }

        }
      }
    }.start()
  })

  Thread.sleep(300)
  terminate = true
}
