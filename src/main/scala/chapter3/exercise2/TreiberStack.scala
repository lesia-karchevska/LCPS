package chapter3.exercise2

import java.util.concurrent.atomic.AtomicReference

/* Chapter 3. Exercise 2. Implement a TreiberStack class, which implements a concurrent stack
abstraction:
class TreiberStack[T] {
  def push(x: T): Unit = ???
  def pop(): T = ???
}
Use an atomic reference variable that points to a linked list of nodes that
were previously pushed to the stack. Make sure that your implementation is
lock-free and not susceptible to the ABA problem.
*/

class TreiberStack[T] {

  private val stack: AtomicReference[List[T]] = new AtomicReference[List[T]](List())

  def push(x: T): Unit = {
    val currentStack = stack.get
    val newStack = x :: currentStack
    if (!stack.compareAndSet(currentStack, newStack)) { println(Thread.currentThread().getName + " failed to execute push(), retry"); push(x) }
    println(Thread.currentThread().getName + " pushed value " + x + "; stack: " + stack.get.toString)
  }

  //seems like putting back xs instead of xs.take(xs.size) also works and doesn't cause ABA problems
  def pop(): Option[T] = {
    val current = stack.get
    current match {
      case x :: xs => {
        if (stack.compareAndSet(current, xs.take(xs.size): List[T])) {
          println(Thread.currentThread().getName + " got value " + x + "; stack: " + stack.get.toString)
          Some(x)
        } else { println(Thread.currentThread().getName + " failed to execute pop(), retry"); pop() }
      }
      case List() => { println(Thread.currentThread().getName + " found no elements in the stack!"); None }
    }
  }
}
