package example

import cats.tagless._
import com.typesafe.scalalogging.StrictLogging

@finalAlg
trait Console[F[_]] {
  def printLn(line: String): F[Unit]
}

object Console extends StrictLogging {
  def console[F[_]: IoSync]: Console[F] =
    (line: String) =>
      IoSync[F].delay {
        logger.debug("Console")
        println(line)
    }
}
