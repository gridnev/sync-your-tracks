
object Utils {
  def toImmutable[A](elements: Iterable[A]) =
    new scala.collection.immutable.Iterable[A] {
      override def iterator: Iterator[A] = elements.toIterator
    }
}
