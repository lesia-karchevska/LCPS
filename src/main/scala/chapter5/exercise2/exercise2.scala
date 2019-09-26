package chapter5.exercise2

import java.util.concurrent.atomic.AtomicInteger

import chapter5.Timed

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.collection.parallel.CollectionConverters._

object exercise2 extends App {

  class SimpleSyncCounter {
    var value: Int = 0
    def +=(n: Int): Unit = {
      this.synchronized {
        value += n
      }
    }
  }

  def getRandomString(p: Double, length: Int): ArrayBuffer[Char] = {

    val rnd = new Random()
    val randomString = new ArrayBuffer[Char](length)
    Range(0, length).foreach(i => {
      val value = rnd.nextInt(100)
      if (value > p) randomString.addOne(rnd.nextPrintableChar())
      else randomString.addOne(' ')
    })
    randomString
  }

  /*Conclusions from the runs:
  1. parallel run is slower here since we are increasing the counter, and this should be synchronized
  2. using atomic variable as a counter is the same as synchronizing
  2. the more frequent the symbol is the less is the running time in each case
  */
  Range(0, 100).foreach(n => {
    val counter = new AtomicInteger(0)
    var seqCounter: Int = 0
    val syncCounter = new SimpleSyncCounter()
    val randomStr = getRandomString(n, 20)
    val parVec = randomStr.toVector.par
    val parTimeAtomic = Timed.timed { parVec.foreach(ch => if (ch.equals(' ')) { counter.incrementAndGet() })}
    val seqTime = Timed.timed { randomStr.foreach(ch => if (ch.equals(' ')) seqCounter += 1 )}
    val parTimeSync = Timed.timed { parVec.foreach(ch => if (ch.equals(' ')) { syncCounter += 1 })}
    println("p = " + n.toDouble / 100.0 + ", par time atomic = " + parTimeAtomic + ", par time sync = " + parTimeSync + ", seq time = " + seqTime + ", count = " + counter.get())
  })
}
