package example

import monix.eval.Task
import monix.execution.Scheduler

trait IoAsync[F[_]] {
  def delay[A](thunk: ⇒ A): F[A]

  def unit: F[Unit]
}

object IoAsync {
  def apply[F[_]](implicit F: IoAsync[F]): IoAsync[F] = F

  def ioAsync(implicit io: Scheduler): IoAsync[Task] = new IoAsync[Task] {
    def delay[A](thunk: ⇒ A): Task[A] =
      Task
        .delay(thunk)
        .executeOn(io)

    def unit: Task[Unit] = Task.unit
  }
}
