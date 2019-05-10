package com.lunatech.iamin

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.{Executors, ThreadFactory}

import cats.effect._
import cats.implicits._
import com.lunatech.iamin.config.Config
import com.lunatech.iamin.database.Database
import com.lunatech.iamin.domain.occasions.OccasionService
import com.lunatech.iamin.domain.users.UserService
import com.lunatech.iamin.endpoints.occasions.OccasionsResource
import com.lunatech.iamin.endpoints.users.UsersResource
import com.lunatech.iamin.endpoints.version.VersionResource
import com.lunatech.iamin.endpoints.{OccasionsHandlerImpl, SwaggerResource, UsersHandlerImpl, VersionHandlerImpl}
import com.lunatech.iamin.repository.{SlickOccasionRepository, SlickUserRepository}
import com.lunatech.iamin.utils.{Banner, BuildInfo, HashidsIdObfuscator}
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext

@SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val server = for {
      config <- Resource.liftF(Config.load())
      db <- Database.create(config.database)
      _ <- Resource.liftF(Database.migrate(db.source.createConnection))
      server = new Server(config, db)
    } yield server

    server.use(_.stream[IO].compile.drain.as(ExitCode.Success))
  }

  class Server(config: Config, db: com.lunatech.iamin.database.Profile.api.Database) {
    def stream[F[_] : ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

      val blockingIOEc = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool(new ThreadFactory {
        private[this] val counter = new AtomicLong(0L)
        override def newThread(r: Runnable): Thread = {
          val th = new Thread(r)
          th.setName("blocking-io-thread-" + counter.getAndIncrement().toString)
          th.setDaemon(true)
          th
        }
      }))

      val idObfuscator = new HashidsIdObfuscator(config.application.hashids)

      val occasionsRepository = new SlickOccasionRepository[F](db)
      val occasionService = new OccasionService[F](occasionsRepository)

      val userRepository = new SlickUserRepository[F](db)
      val userService = new UserService[F](userRepository)

      val httpApp = (
        new OccasionsResource[F].routes(new OccasionsHandlerImpl[F](occasionService, idObfuscator)) <+>
          new UsersResource[F].routes(new UsersHandlerImpl[F](userService, idObfuscator)) <+>
          new VersionResource[F].routes(new VersionHandlerImpl[F](BuildInfo)) <+>
          new SwaggerResource[F](blockingIOEc).routes()
        ).orNotFound
      val finalHttpApp = Logger.httpApp(logHeaders = true, logBody = false)(httpApp)

      BlazeServerBuilder[F]
        .withBanner(Banner.banner.split("\n").toList)
        .bindHttp(config.server.port, config.server.host)
        .withHttpApp(finalHttpApp)
        .serve
        .drain
    }
  }
}
