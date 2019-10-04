package example

import cats.effect.{ContextShift, IO}
import cats.tagless._
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.ExecutionContext

@finalAlg
trait IoSync[F[_]] {
  def delay[A](thunk: => A): F[A]

  def unit: F[Unit]
}

object IoSync {
  def task(implicit io: Scheduler): IoSync[Task] = new IoSync[Task] {
    def delay[A](thunk: => A): Task[A] =
      Task
        .delay(thunk)
        .executeOn(io)

    def unit: Task[Unit] = Task.unit
  }

  def io(ioScheduler: ExecutionContext)(implicit cs: ContextShift[IO]): IoSync[IO] =
    new IoSync[IO] {
      def delay[A](thunk: => A): IO[A] =
        cs.evalOn(ioScheduler)(IO.delay(thunk))

      def unit: IO[Unit] = IO.unit
    }
}
