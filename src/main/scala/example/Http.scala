package example

import akka.actor.ActorSystem
import akka.http.scaladsl
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import monix.eval.Task

trait Http[F[_]] {
  def bindAndHandle(route: Flow[HttpRequest, HttpResponse, Any]): F[ServerBinding]
}

object Http {
  def apply[F[_]](implicit F: Http[F]): Http[F] = F

  def http(implicit as: ActorSystem, mat: Materializer): Http[Task] =
    (route: Flow[HttpRequest, HttpResponse, Any]) ⇒
      Task.deferFuture(scaladsl.Http().bindAndHandle(route, "0.0.0.0", 8080))
}
