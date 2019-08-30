package chapter2.exercises3_6

object Exercises3_6 extends App {

  //testing SyncVar
  val sv = new SyncVar[Int]
  val cons = new Consumer(sv)(sv => println(sv.get().toString()))
  val prod = new Producer(sv)

  //ordinary consumers and producers
  prod.produce(List.range(0, 3))
  cons.consume()

  //waiting consumers and producers
  val wsv = new SyncVar[Int]
  val wcons = new WaitingConsumer(wsv)(v => println(v.getOrElse("cancelling...").toString()))
  val wprod = new WaitingProducer(wsv)

  wprod.produce(List.range(0, 15))
  wcons.consume()

  //testing SyncQueue

  val wsq = new SyncQueue[Int]
  wsq.setCapacity(5)

  val qwcons = new WaitingConsumer(wsq)(v => println(v.getOrElse("cancelling...").toString()))
  val qwprod = new WaitingProducer(wsq)

  qwprod.produce(List.range(0, 25))
  qwcons.consume()
}




