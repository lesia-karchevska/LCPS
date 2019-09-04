package chapter2.exercises8_10

import scala.annotation.tailrec
import scala.collection.mutable


class PriorityQueuePool{

  def this(capacity: Int) = {
    this()
    workers = new mutable.HashSet[Worker]()
    List.range(0, capacity).foreach(i => { val w: Worker = new Worker("worker " + i.toString()); w.start(); workers.add(w); })
  }

  @volatile var running: Boolean = true
  private val queue: PrioritySyncQueue = new PrioritySyncQueue
  private var workers: mutable.HashSet[Worker] = _


  @tailrec
  private def runWorker(w: Worker): Unit = {
    queue.getWait() match {
      case Some(task) => {
        println(w.name + " started execution of a task with priority " + task.priority)
        task.task(); println(w.name + " finished execution of a task with priority " + task.priority); runWorker(w)
      }
      case None =>
    }
  }

  def shutdown(priority: Int): Unit = {
    running = false
    val lock = queue.getSyncLock()
    lock.synchronized {
      queue.remove(priority)
      while (!queue.isEmpty()) lock.wait()
      queue.cancel = true
      lock.notifyAll()
    }
  }

  def execute(t: PriorityTask) = {
    if (running) {
      queue.put(t)
    } else {
      println("shutdown initiated, accept no more tasks");
    }
  }

  def execute(tasks: Seq[PriorityTask]) = {
    if (running) {
      queue.putWait(tasks)
    } else {
      println("shutdown initiated, accept no more tasks");
    }
  }

  class Worker extends Runnable {

    def this(name: String) ={
      this()
      this.name = name
    }

    var name: String = _

    val thread: Thread = new Thread(this)
    def run() = { runWorker(this) }
    def start(): Unit = { thread.start() }
  }

}
