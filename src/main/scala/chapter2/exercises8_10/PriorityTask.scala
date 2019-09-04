package chapter2.exercises8_10

class PriorityTask extends Ordered[PriorityTask] {

  var priority: Int = 0
  var task: () => Unit = () => {}

  def this(priority: Int, task:() =>Unit) {
    this()
    this.priority = priority
    this.task = task
  }

  def compare(that: PriorityTask): Int = {
    this.priority - that.priority
  }
}
