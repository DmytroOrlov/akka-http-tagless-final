package zioapp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import scalaz.zio.ZIO

package object http extends Http.Service[Http] {
  def bindAndHandle(route: Flow[HttpRequest, HttpResponse, Any])(
      implicit system: ActorSystem,
      mat: Materializer): ZIO[Http, Throwable, ServerBinding] =
    ZIO.accessM(_.http bindAndHandle route)
}
