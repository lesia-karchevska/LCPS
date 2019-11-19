package chapter6.exercise1

object ThreadDetectorObservable {

/*  def createThreadObservable(cancellationToken: CancellationToken): Observable[Thread] = {

    var threads = Thread.getAllStackTraces().keySet()

    def getThreadsStartedSinceLastUpdate() = {

      val threadSet = Thread.getAllStackTraces().keySet()
      val recentlyStarted = threadSet
      recentlyStarted.removeAll(threads)
      threads = threadSet
      recentlyStarted
    }

    Observable.create[Thread](new OnSubscribe[Thread] {
      override def call(t1: Subscriber[_ >:Thread]): Unit = {
        new Thread(() => {
          while (!cancellationToken.cancel) {
            Thread.sleep(500)
            getThreadsStartedSinceLastUpdate.forEach(t => {
              t1.onNext(t)
            })
          }
        }).start()
      }
    })

    /*Observable.create[Thread] {
      (obs: Subscriber[_ >:Thread]) => {
        new Thread(() => {
          while (!cancellationToken.cancel) {
            Thread.sleep(500)
            getThreadsStartedSinceLastUpdate.forEach(t => {
              obs.onNext(t)
            })
          }
        }).start()
        new Subscription() {
          private val unsubscribed = new AtomicBoolean(false)
          override def unsubscribe(): Unit = {
            cancellationToken.cancel = true
            unsubscribed.compareAndSet(false, true)
          }
          override def isUnsubscribed: Boolean = {
            unsubscribed.get()
          }
        }
      }
    } */
  }*/
}
