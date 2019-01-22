package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import cats.effect.IO
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import example.Marshallable._
import io.circe.generic.auto._
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Future

object Launcher extends App {
  def route[F[_]: Database: Marshallable] = get {
    path("users" / IntNumber) { id =>
      complete(Database[F].load(id))
    }
  }

  type Effect[A] = Task[A]
  implicit val db = Database.database[Effect]

  implicit val system: ActorSystem = ActorSystem("akka-http")
  implicit val mat: Materializer = ActorMaterializer()
  implicit val sc: Scheduler = Scheduler(system.dispatcher)

  println("starting")
  val binding = Http().bindAndHandle(route, "0.0.0.0", 8080)
  println("started")
}
