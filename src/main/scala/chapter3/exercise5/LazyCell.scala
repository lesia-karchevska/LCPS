package chapter3.exercise5

class LazyCell[T](initialization: =>T) {

  @volatile private var set = false
  private var value: T = _
  private val lock = new AnyRef

  def apply(): T = {
    if (set) value
    else {
      lock.synchronized {
        if (!set) {
          value = initialization
          set = true
        }
        value
      }
    }
  }
}
