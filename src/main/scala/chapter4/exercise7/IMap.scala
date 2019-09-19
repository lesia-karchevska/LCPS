package chapter4.exercise7

import scala.concurrent.{Future, Promise}

class IMap[K, V] {

  private val map = new scala.collection.concurrent.TrieMap[K, Promise[V]]

  private def getPromise(k: K): Promise[V] = {
    val p = Promise[V]
    map.putIfAbsent(k, p) match {
      case Some(old_p) => old_p
      case None => p
    }
  }
  def update(k: K, v: V): Unit = {
    getPromise(k) success v
  }

  def apply(k: K): Future[V] = {
    getPromise(k).future
  }
}
