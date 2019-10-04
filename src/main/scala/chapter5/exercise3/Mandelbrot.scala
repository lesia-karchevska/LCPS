package chapter5.exercise3

object Mandelbrot {

  def getIteration(a: Double, b: Double, max_iteration: Integer): Int = {
    var x = 0.0
    var y = 0.0
    var n = 0

    while(math.sqrt(x * x + y * y) <= 2 && n < max_iteration) {
      val temp = x * x - y * y + a
      y = 2 * x * y + b
      x = temp
      n += 1
    }

    if (n == max_iteration) max_iteration
    else n + 1
  }

}
