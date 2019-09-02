package chapter2.exercise7

import chapter2.exercise7.SynchronizedUniqueId.getUniqueId

class Account(val name: String, var money: Int) {
  val uid: Long = getUniqueId()
  def add(account: Account, n: Int) = account.synchronized {
    account.money += n
  }
}

