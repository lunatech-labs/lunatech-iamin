package com.lunatech.iamin.rest

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.lunatech.iamin.config.Config
import com.lunatech.iamin.utils.DatabaseMigrator

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    IO(DatabaseMigrator.applyMigrations(Config.database)).flatMap {
      case Left(_) =>
        IO.pure(ExitCode.Error)
      case Right(_) =>
        IaminServer.stream[IO].compile.drain.as(ExitCode.Success)
    }
}
