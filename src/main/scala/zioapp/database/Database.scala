package zioapp.database

import com.typesafe.scalalogging.StrictLogging
import example.User
import zio._

trait Database {
  def database: Database.Service[Any]
}

object Database {

  trait Service[R] {
    def load(id: Int): ZIO[R, Throwable, User]

    def save(user: User): ZIO[R, Throwable, Unit]
  }

  trait Live extends Database with StrictLogging {
    val database = new Service[Any] {
      def load(id: Int): Task[User] = IO.effect {
        logger.debug("Database")
        User(id)
      }

      def save(user: User): Task[Unit] = IO.effect { () }
    }
  }

  object Live extends Live
}
