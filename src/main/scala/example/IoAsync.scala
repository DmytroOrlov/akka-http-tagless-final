package example

import cats.tagless._
import monix.eval.Task
import monix.execution.Scheduler

@finalAlg
trait IoAsync[F[_]] {
  def delay[A](thunk: ⇒ A): F[A]

  def unit: F[Unit]
}

object IoAsync {
  def ioAsync(implicit io: Scheduler): IoAsync[Task] = new IoAsync[Task] {
    def delay[A](thunk: ⇒ A): Task[A] =
      Task
        .delay(thunk)
        .executeOn(io)

    def unit: Task[Unit] = Task.unit
  }
}
