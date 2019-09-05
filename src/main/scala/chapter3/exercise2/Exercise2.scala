package chapter3.exercise2

object Exercise2 extends App {
  val stack:TreiberStack[Int] = new TreiberStack[Int]
  Thread.currentThread().setName("main")
  List.range(0, 10).foreach(i => stack.push(i))
  @volatile var terminate = false

  List.range(0, 10).foreach(i => {
    new Thread(){
      override def run() ={
        this.setName("thread-" + i)
        val random = new scala.util.Random()
        while (!terminate) {
          stack.pop()
          Thread.sleep(random.nextInt(10))
          stack.push(random.nextInt(15))
        }
      }
    }.start()
  })
  Thread.sleep(1000);
  terminate = true
  Thread.sleep(1000)

  println("exit")
}
