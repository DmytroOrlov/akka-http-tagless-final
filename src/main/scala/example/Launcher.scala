package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import cats.Monad
import cats.effect.IO
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport.marshaller
import example.Marshallable.marshal
import io.circe.generic.auto._
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._

object Launcher extends App {
  def route[R[_]: Database: Marshallable] =
    get {
      path("users" / IntNumber) { id ⇒
        complete(Database[R].load(id))
      }
    }

  implicit val system: ActorSystem = ActorSystem("akka-http")
  implicit val mat: Materializer = ActorMaterializer()
  implicit val sc: Scheduler = Scheduler(system.dispatcher)

  def program[F[_]: Console: Http: Monad, R[_]: Database: Marshallable]: F[ServerBinding] =
    for {
      _ <- Console[F].printLn("starting...")
      binding <- Http[F].bindAndHandle(route[R])
      _ <- Console[F].printLn("started")
    } yield binding

  type Effect0[A] = Task[A]
  type Effect[A] = Task[A]

  val app: Effect[_] = {
    implicit val io = IoAsync.ioAsync(Scheduler.io("io-scheduler"))
    implicit val database = Database.database[Effect]
    implicit val console = Console.console[Effect]
    implicit val http = Http.http

    program[Effect, Effect0]
  }

  val DefaultShutdownTimeout = 29.seconds

  (app.attempt >>= {
    case Left(_) ⇒
      Task
        .deferFuture(system.terminate())
        .timeout(DefaultShutdownTimeout)
        .map(_ ⇒ System.exit(1))
    case Right(_) ⇒
      ().pure[Effect]
  }).runSyncUnsafe()
}
