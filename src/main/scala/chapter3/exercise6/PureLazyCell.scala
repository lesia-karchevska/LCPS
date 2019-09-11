package chapter3.exercise6

import java.util.concurrent.atomic.AtomicReference

class PureLazyCell[T](initialization: =>T) {

  private val value: AtomicReference[Option[T]] = new AtomicReference(None)

  def apply(): T = {
    value.get match {
      case Some(v) => v
      case None =>
        val newVal = initialization
        if (value.compareAndSet(None, Some(newVal))) {
          newVal
        } else apply()
    }
  }

}
