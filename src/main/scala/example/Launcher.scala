package example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import cats.Monad
import cats.effect.IO
import cats.implicits._
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport.marshaller
import example.Marshallable.marshal
import io.circe.generic.auto._
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._

object Launcher extends App with StrictLogging {
  def route[R[_]: Database: Marshallable] =
    get {
      path("users" / IntNumber) { id ⇒
        logger.debug(s"request id=$id")
        complete(Database[R].load(id))
      }
    }

  implicit val system: ActorSystem = ActorSystem("akka-http")
  implicit val mat: Materializer = ActorMaterializer()
  implicit val sc: Scheduler = Scheduler(system.dispatcher)
  implicit val cs = IO.contextShift(sc)
  implicit val timer = IO.timer(sc)

  def program[F[_]: Http: Console: Monad, R[_]: Database: Marshallable]: F[ServerBinding] =
    for {
      _ <- Console[F].printLn("starting...")
      binding <- Http[F].bindAndHandle(route[R])
      _ <- Console[F].printLn("started")
    } yield binding

  type EffectR[A] = Task[A]
  type Effect[A] = Task[A]

  val app: Effect[_] = {
    implicit val io = IoSync.task(Scheduler.io("io-scheduler"))
    implicit val http = Http.task
    implicit val database = Database.database[Effect]
    implicit val console = Console.console[Effect]

    program[Effect, EffectR]
  }

  val DefaultShutdownTimeout = 29.seconds

  (app.attempt >>= {
    case Left(_) ⇒
      Task
        .deferFuture(system.terminate())
        .timeoutTo(DefaultShutdownTimeout, Task.unit)
        .map(_ ⇒ sys.exit(1))
    case Right(_) ⇒
      ().pure[Effect]
  }).runSyncUnsafe()
}
