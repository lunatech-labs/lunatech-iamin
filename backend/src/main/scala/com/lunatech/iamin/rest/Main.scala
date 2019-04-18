package com.lunatech.iamin.rest

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import com.lunatech.iamin.config.Config
import com.lunatech.iamin.database.Database

object Main extends IOApp {

  @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
  override def run(args: List[String]): IO[ExitCode] = {

    val server = for {
      config <- Resource.liftF(Config.load())
      db     <- Database.create(config.database)
      _      <- Resource.liftF(Database.migrate(db.source.createConnection))
      server = new IaminServer(config, db)
    } yield server

    server.use(_.stream[IO].compile.drain.as(ExitCode.Success))
  }
}
