package chapter2.exercises8_10

object Exercises8_10 extends App {
  val t1 = new PriorityTask(1, () => { Thread.sleep(60); println("task 1 executing") })
  val t2 = new PriorityTask(2, () => { Thread.sleep(55); println("task 2 executing") })
  val t3 = new PriorityTask(1, () => { Thread.sleep(60); println("task 3 executing") })
  val t4 = new PriorityTask(3, () => { Thread.sleep(70); println("task 4 executing") })
  val t5 = new PriorityTask(2, () => { Thread.sleep(70); println("task 5 executing") })

  val pool = new PriorityQueuePool(2)
  pool.execute(Seq(t1, t2, t3, t4))

  pool.shutdown(2)
  pool.execute(t5)
}
