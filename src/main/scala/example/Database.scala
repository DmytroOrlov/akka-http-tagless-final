package example

import cats.Applicative
import cats.effect.Async

trait Database[F[_]] {
  def load(id: Int): F[User]

  def save(user: User): F[Unit]
}

object Database {
  def apply[F[_]](implicit F: Database[F]): Database[F] = F

  def database[F[_]: Async]: Database[F] = new Database[F] {
    def load(id: Int): F[User] = Async[F].delay(User(id))

    def save(user: User): F[Unit] = Async[F].unit
  }

  def future[F[_]: Applicative]: Database[F] = new Database[F] {
    def load(id: Int): F[User] = Applicative[F].pure(User(id))

    def save(user: User): F[Unit] = Applicative[F].unit
  }
}

case class User(id: Int)
