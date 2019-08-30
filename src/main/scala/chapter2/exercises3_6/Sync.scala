package chapter2.exercises3_6

trait Sync[T] {

  @volatile var cancel: Boolean
  def get(): T
  def put(v: T)
  def getWait(): Option[T]
  def putWait(v: T)
  def getSyncLock(): AnyRef
}
