package chapter2.exercise7

object Exercise7 extends App{

  val acc1 = new Account("Anna", 25)
  val acc2 = new Account("Larry", 15)
  val acc3 = new Account("Sam", 10)
  val acc4 = new Account("Harry", 12)

  val accs1:Set[Account] = Set(acc2, acc3, acc1)
  val accs2:Set[Account] = Set(acc4, acc1, acc3)
  val target = new Account("Bill", 5)
  new Thread(() => Transferor.sendAllInSingleTransaction(accs1, target)).start()
  new Thread(() => Transferor.sendAllInSingleTransaction(accs2, target)).start()
}
