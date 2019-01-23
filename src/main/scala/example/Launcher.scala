package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import cats.{Applicative, Monad}
import cats.effect.IO
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import example.Marshallable._
import io.circe.generic.auto._
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Future

object Launcher extends App {
  def routable[F[_]: Database: Marshallable: Applicative] = Applicative[F].pure(
    get {
      path("users" / IntNumber) { id =>
        complete(Database[F].load(id))
      }
    }
  )

  implicit val system: ActorSystem = ActorSystem("akka-http")
  implicit val mat: Materializer = ActorMaterializer()
  implicit val sc: Scheduler = Scheduler(system.dispatcher)

  def bind[F[_]: Database: Console: Http: Marshallable: Monad]: F[ServerBinding] =
    for {
      _ <- Console[F].printLn("starting...")
      route <- routable
      binding <- Http[F].bindAndHandle(route)
      _ <- Console[F].printLn("started")
    } yield binding

  def program = {
    type Effect[A] = Task[A]
    implicit val database = Database.database[Effect]
    implicit val console = Console.console[Effect]
    implicit val http = Http.http

    bind[Effect]
  }

  program.runSyncUnsafe()
}
