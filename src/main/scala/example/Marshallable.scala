package example

import akka.http.scaladsl.marshalling.GenericMarshallers.futureMarshaller
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import cats.effect.IO
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Future

trait Marshallable[F[_]] {
  def apply[A](implicit m: ToResponseMarshaller[A]): ToResponseMarshaller[F[A]]
}

object Marshallable {
  implicit def apply[F[_], A: ToResponseMarshaller](
      implicit M: Marshallable[F]): ToResponseMarshaller[F[A]] = M.apply

  implicit val marshallableFuture: Marshallable[Future] =
    new Marshallable[Future] {
      def apply[A](implicit m: ToResponseMarshaller[A]): ToResponseMarshaller[Future[A]] =
        implicitly
    }

  implicit def marshallableTask(implicit sc: Scheduler): Marshallable[Task] =
    new Marshallable[Task] {
      def apply[A](implicit m: ToResponseMarshaller[A]): ToResponseMarshaller[Task[A]] =
        implicitly[ToResponseMarshaller[Future[A]]].compose(_.runToFuture)
    }

  implicit val marshallableIO: Marshallable[IO] = new Marshallable[IO] {
    def apply[A](implicit m: ToResponseMarshaller[A]): ToResponseMarshaller[IO[A]] =
      implicitly[ToResponseMarshaller[Future[A]]].compose(io ⇒ io.unsafeToFuture())
  }
}
