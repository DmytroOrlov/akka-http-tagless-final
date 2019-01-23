package example

import cats.effect.Async

trait Console[F[_]] {
  def printLn(line: String): F[Unit]
}

object Console {
  def apply[F[_]](implicit console: Console[F]): Console[F] = console

  def console[F[_]: Async]: Console[F] =
    (line: String) => Async[F].delay(println(line))
}
