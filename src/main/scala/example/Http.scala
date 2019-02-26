package example

import akka.actor.ActorSystem
import akka.http.scaladsl
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import cats.effect.IO
import cats.tagless._
import monix.eval.Task

@finalAlg
trait Http[F[_]] {
  def bindAndHandle(route: Flow[HttpRequest, HttpResponse, Any]): F[ServerBinding]
}

object Http {
  def task(implicit as: ActorSystem, mat: Materializer): Http[Task] =
    (route: Flow[HttpRequest, HttpResponse, Any]) ⇒
      Task.deferFuture(scaladsl.Http().bindAndHandle(route, "0.0.0.0", 8080))

  def io(implicit as: ActorSystem, mat: Materializer): Http[IO] =
    (route: Flow[HttpRequest, HttpResponse, Any]) ⇒
      IO.fromFuture(IO.delay(scaladsl.Http().bindAndHandle(route, "0.0.0.0", 8080)))
}
