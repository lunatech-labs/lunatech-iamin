package com.lunatech.iamin.rest

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lunatech.iamin.config.Config
import com.lunatech.iamin.database.Database

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      config   <- Config.load("application.conf")
      xa       = Database.transactor(config.database)
      _        <- xa.use(Database.init)
      server   = new IaminServer(config.server)
      exitCode <- server.stream[IO].compile.drain.as(ExitCode.Success)
    } yield exitCode
  }
}
