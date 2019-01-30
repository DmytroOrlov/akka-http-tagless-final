package example

import cats.Applicative
import cats.syntax.applicative._
import cats.tagless._

@finalAlg
trait Database[F[_]] {
  def load(id: Int): F[User]

  def save(user: User): F[Unit]
}

object Database {
  def database[F[_]: IoAsync]: Database[F] = new Database[F] {
    def load(id: Int): F[User] = IoAsync[F].delay(User(id))

    def save(user: User): F[Unit] = IoAsync[F].unit
  }

  def future[F[_]: Applicative]: Database[F] = new Database[F] {
    def load(id: Int): F[User] = User(id).pure[F]

    def save(user: User): F[Unit] = Applicative[F].unit
  }
}

case class User(id: Int)
