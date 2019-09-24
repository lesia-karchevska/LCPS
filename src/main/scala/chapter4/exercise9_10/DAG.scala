package chapter4.exercise9_10

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DAG[T](val value: T) {
  val edges: mutable.Set[DAG[T]] = new mutable.HashSet[DAG[T]]()
}

object DAG {

  //this version is not that efficient: we don't save the result computed for a node at some point
  //multiple nodes can depend on that node, there might be no need to recalculate the value
  //many threads are needed to execute this, threads are blocked until computations they depend on finish
  def fold[T, S](g: DAG[T], f: (T, Seq[S]) => S): Future[S] = {
    if (g.edges.size == 0) Future { f(g.value, Seq()) }
    else {
      g.edges.map(edge => fold(edge, f))
        .foldLeft(Future { Seq(): Seq[S] })((fs, f) => fs.flatMap(s => f.map(res => res +: s)))
        .map(seq => f(g.value, seq))
    }
  }
}
