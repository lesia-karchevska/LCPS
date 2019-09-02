package chapter2.exercise7

import scala.collection.Set

object Transferor {

  def sendAll(locked: List[Account], remaining: List[Account], target: Account): Unit = {
    remaining match {
      case Nil => {
        locked.foreach(a => {
          if (!a.equals(target)) {
            target.money += a.money
            println("transfer: " + a.name + "->" + target.name + ", amount: " + a.money)
            a.money = 0
          }
        })
      }
      case x::xs => {
        x.synchronized {
          sendAll(x +: locked, xs, target)
        }
      }
    }
  }

  def sendFromOne(from: Account, target: Account) = {
    if (from.uid < target.uid) {
      from.synchronized {
        target.synchronized {
          target.money += from.money
          from.money = 0
        }
      }
    } else {
      target.synchronized {
        from.synchronized {
          target.money += from.money
          from.money = 0
        }
      }
    }
  }

  def sendAllInSingleTransaction(accounts: Set[Account], target: Account): Unit = {
    var accList = accounts.toList
    accList = target +: accList
    sendAll(Nil, accList.sortBy(a => a.uid), target)
  }

  def sendOneByOne(accounts: Set[Account], target: Account) = {
    accounts.toList.foreach(a => sendFromOne(a, target))
  }
}
