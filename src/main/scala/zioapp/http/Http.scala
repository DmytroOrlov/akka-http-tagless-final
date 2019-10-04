package zioapp.http

import akka.actor.ActorSystem
import akka.http.scaladsl
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import zio._

trait Http {
  def http: Http.Service[Any]
}

object Http {

  trait Service[R] {
    def bindAndHandle(route: Flow[HttpRequest, HttpResponse, Any])(
        implicit system: ActorSystem,
        mat: Materializer): ZIO[R, Throwable, ServerBinding]
  }

  trait Live extends Http {
    val http = new Service[Any] {
      def bindAndHandle(route: Flow[HttpRequest, HttpResponse, Any])(
          implicit system: ActorSystem,
          mat: Materializer): Task[ServerBinding] =
        IO.fromFuture(_ => scaladsl.Http().bindAndHandle(route, "0.0.0.0", 8080))
    }
  }

}
