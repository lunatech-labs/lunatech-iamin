package com.lunatech.iamin.database

import java.sql.Connection

import cats.effect.{IO, Resource}
import com.lunatech.iamin.config.DatabaseConfig
import com.lunatech.iamin.database
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

import scala.util.{Failure, Success, Try}

object Database {

  def create(config: DatabaseConfig): Resource[IO, database.Profile.backend.DatabaseDef] = {
    Resource.make {
      val driver = new org.postgresql.Driver()
      IO(Profile.api.Database.forDriver(driver, config.url, config.user, config.password))
    } { db =>
      IO(db.close)
    }
  }

  def migrate(connection: Connection): IO[Unit] = IO {
    Try {
      val liquibase = new Liquibase(
        "db_changelog.xml",
        new ClassLoaderResourceAccessor(),
        new JdbcConnection(connection)
      )

      @SuppressWarnings(Array("org.wartremover.warts.Null"))
      val contexts = Option.empty[String].orNull

      liquibase.update(contexts)
    }
  } flatMap {
    case Failure(e) => IO.raiseError[Unit](e)
    case Success(_) => IO.unit
  }
}
