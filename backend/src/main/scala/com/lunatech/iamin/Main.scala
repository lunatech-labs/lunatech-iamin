package com.lunatech.iamin

import cats.effect.ExitCode
import cats.syntax.semigroupk._
import com.lunatech.iamin.config.{Config, HashidsConfig}
import com.lunatech.iamin.core.idcodec.service.{HashidsIdCodec, IdCodec}
import com.lunatech.iamin.core.occasion.repository.{DoobieOccasionRepository, OccasionRepository}
import com.lunatech.iamin.core.user.repository.{DoobieUserRepository, UserRepository}
import com.lunatech.iamin.http.{OccasionEndpoints, SwaggerEndpoints, UserEndpoints, VersionEndpoints}
import com.lunatech.iamin.utils.{Banner, Database, PrettyPrinter}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, CORSConfig}
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console.{Console, putStrLn}
import scalaz.zio.interop.catz._
import scalaz.zio.scheduler.Scheduler
import scalaz.zio.{App, Task, TaskR, ZIO}
import scala.concurrent.duration._

object Main extends App with PrettyPrinter {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {

    type AppEnvironment = Clock with Console with Blocking with UserRepository with OccasionRepository with IdCodec
    type AppTask[A] = TaskR[AppEnvironment, A]

    val corsConfig = CORSConfig(
      anyOrigin = true,
      anyMethod = false,
      allowedOrigins = Set("localhost", "localhost:5000"),
      allowedMethods = Some(Set("GET", "POST", "DELETE", "PUT", "PATCH")),
      allowCredentials = true,
      maxAge = 1.day.toSeconds)

    val program = for {
      config      <- ZIO.fromEither(Config.load)
      _           <- putStrLn(s"Loaded configuration: ${prettyPrint(config)}")
      _           <- Database.runMigrations(config.database)
      blockingEc  <- ZIO.environment[Blocking].flatMap(_.blocking.blockingExecutor).map(_.asEC)
      connectEc    = Platform.executor.asEC
      transactorR  = Database.mkTransactor(config.database, connectEc, blockingEc)
      httpApp      = CORS(
        new OccasionEndpoints[AppEnvironment]().routes <+>
        new SwaggerEndpoints[AppEnvironment](blockingEc).routes <+>
        new UserEndpoints[AppEnvironment]().routes <+>
        new VersionEndpoints[AppEnvironment]().routes, corsConfig
      ).orNotFound
      server       = ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
        BlazeServerBuilder[AppTask]
          .withBanner(Banner.banner.split("\n").toList)
          .bindHttp(config.server.port, config.server.host)
          .withHttpApp(httpApp)
          .serve
          .compile[AppTask, AppTask, ExitCode]
          .drain
      }
      program     <- transactorR.use { transactor =>
        server.provideSome[Environment] { base =>
          new Clock with Console with Blocking with DoobieUserRepository with DoobieOccasionRepository with HashidsIdCodec {
            override val clock: Clock.Service[Any] = base.clock
            override val console: Console.Service[Any] = base.console
            override val blocking: Blocking.Service[Any] = base.blocking
            override val scheduler: Scheduler.Service[Any] = base.scheduler

            override protected def xa: doobie.Transactor[Task] = transactor

            override protected def hashidsConfig: HashidsConfig = config.application.hashids
          }
        }
      }
    } yield program

    program.foldM(
      e => putStrLn(s"Execution failed with error $e") *> ZIO.succeed(1),
      _ => ZIO.succeed(0)
    )
  }
}
