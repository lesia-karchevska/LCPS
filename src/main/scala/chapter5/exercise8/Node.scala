package chapter5.exercise8

class Node[T] {
  var key: Option[T] = None
  var parent: Option[Node[T]] = None
  var sibling: Option[Node[T]] = None
  var child: Option[Node[T]] = None
  var degree: Int = 0
}