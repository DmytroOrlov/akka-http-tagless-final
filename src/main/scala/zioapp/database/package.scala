package zioapp

import example.User
import scalaz.zio.ZIO

package object database extends Database.Service[Database] {
  def load(id: Int): ZIO[Database, Throwable, User] =
    ZIO.accessM(_.database load id)

  def save(user: User): ZIO[Database, Throwable, Unit] =
    ZIO.accessM(_.database save user)
}
