package example

import cats.effect.Async

import scala.concurrent.Future

trait Database[F[_]] {
  def load(id: Int): F[User]

  def save(user: User): F[Unit]
}

object Database {
  def apply[F[_]](implicit F: Database[F]): Database[F] = F

  def database[F[_]: Async]: Database[F] = new Database[F] {
    def load(id: Int): F[User] = Async[F].pure(User(id))

    def save(user: User): F[Unit] = Async[F].unit
  }

  def future: Database[Future] = new Database[Future] {
    override def load(id: Int): Future[User] = Future.successful(User(id))

    override def save(user: User): Future[Unit] = Future.unit
  }
}

case class User(id: Int)
