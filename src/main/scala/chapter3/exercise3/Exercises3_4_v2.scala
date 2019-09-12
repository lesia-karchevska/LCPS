package chapter3.exercise3

object Exercises3_4_v2 extends App {
  val list = new ConcurrentSortedList_v2[Int]()
  @volatile var terminate = false

  List.range(0, 10).foreach(i => {
    new Thread(){

      override def run() = {
        this.setName("thread-" + i)
        val random = new scala.util.Random()
        while (!terminate) {
          Thread.sleep(random.nextInt(10))
          val x = random.nextInt(15)
          list.add(x)
          println(this.getName + " added " + x + " to concurrent sorted list")
        }
      }
    }.start()
  })
  Thread.sleep(10)
  terminate = true

  val iter = list.iterator
  while(iter.hasNext) println(iter.next)
}
