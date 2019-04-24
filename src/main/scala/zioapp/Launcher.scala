package zioapp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.GenericMarshallers.futureMarshaller
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.scalalogging.StrictLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport.marshaller
import io.circe.generic.auto._
import scalaz.zio._
import scalaz.zio.clock.Clock
import scalaz.zio.console._
import scalaz.zio.duration._
import zioapp.database._
import zioapp.http._

import scala.concurrent.Future

object Launcher extends scala.App with DefaultRuntime with StrictLogging {
  implicit def zioMarshaller[A, B, R](implicit futureMarshaller: Marshaller[Future[A], B],
                                      r: R): Marshaller[ZIO[R, Throwable, A], B] =
    futureMarshaller.compose(io ⇒ unsafeRunToFuture(io.provide(r)))

  implicit val system: ActorSystem = ActorSystem("akka-http")
  implicit val mat: Materializer = ActorMaterializer()
  implicit val environment = Database.Live

  val route = get {
    path("users" / IntNumber) { id ⇒
      logger.debug(s"request id=$id")
      complete(database.load(id))
    }
  }

  val program = for {
    _ <- putStrLn("starting...")
    _ <- http.bindAndHandle(route)
    _ <- putStrLn("started")
  } yield ()

  val DefaultShutdownTimeout = 29.seconds

  val app = program.catchAll { e ⇒
    logger.error("terminate", e)
    IO.fromFuture(_ ⇒ system.terminate()).timeout(DefaultShutdownTimeout) *>
      IO.effectTotal(sys.exit(1))
  }

  unsafeRun(
    app.provideSome[Console with Clock](c ⇒
      new Console with Clock with Http.Live {
        val console = c.console
        val clock = c.clock
        val scheduler = c.scheduler
    })
  )
}
