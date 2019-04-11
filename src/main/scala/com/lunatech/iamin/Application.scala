package com.lunatech.iamin

import cats.effect.{Effect, IO}
import com.lunatech.iamin.config.Config
import com.lunatech.iamin.rest.HelloWorldService
import com.lunatech.iamin.utils.DatabaseMigrator
import fs2.StreamApp
import fs2.StreamApp.ExitCode
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object Application extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, ExitCode] = {
      fs2.Stream.eval(IO(DatabaseMigrator.applyMigrations(Config.database)))
        .flatMap {
          case Left(_) => fs2.Stream.eval(IO.pure(ExitCode.Error))
          case Right(_) => ServerStream.stream[IO]
        }
  }
}

object ServerStream {

  def helloWorldService[F[_]: Effect]: HttpService[F] =
    new HelloWorldService[F].service

  def stream[F[_]: Effect](implicit ec: ExecutionContext): fs2.Stream[F, ExitCode] =
    BlazeBuilder[F]
      .bindHttp(Config.server.port, Config.server.host)
      .mountService(helloWorldService, "/")
      .serve
}