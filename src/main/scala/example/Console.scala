package example

import cats.tagless._

@finalAlg
trait Console[F[_]] {
  def printLn(line: String): F[Unit]
}

object Console {
  def console[F[_]: IoAsync]: Console[F] =
    (line: String) ⇒ IoAsync[F].delay(println(line))
}
