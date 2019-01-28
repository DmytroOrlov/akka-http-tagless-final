package example

trait Console[F[_]] {
  def printLn(line: String): F[Unit]
}

object Console {
  def apply[F[_]](implicit F: Console[F]): Console[F] = F

  def console[F[_]: IoAsync]: Console[F] =
    (line: String) ⇒ IoAsync[F].delay(println(line))
}
