package example

import cats.Applicative
import cats.syntax.applicative._
import cats.tagless._
import com.typesafe.scalalogging.StrictLogging

@finalAlg
trait Database[F[_]] {
  def load(id: Int): F[User]

  def save(user: User): F[Unit]
}

object Database extends StrictLogging {
  def database[F[_]: IoSync]: Database[F] = new Database[F] {
    def load(id: Int): F[User] = IoSync[F].delay {
      logger.debug("Database")
      User(id)
    }

    def save(user: User): F[Unit] = IoSync[F].unit
  }

  def future[F[_]: Applicative]: Database[F] = new Database[F] {
    def load(id: Int): F[User] = User(id).pure[F]

    def save(user: User): F[Unit] = Applicative[F].unit
  }
}

case class User(id: Int)
