package com.lunatech.iamin.database

import java.sql.Connection

import cats.effect.{ContextShift, IO, Resource}
import cats.implicits._
import com.lunatech.iamin.config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

object Database {

  def transactor(config: DatabaseConfig): Resource[IO, HikariTransactor[IO]] = {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      te <- ExecutionContexts.cachedThreadPool[IO]
      xa <- HikariTransactor.newHikariTransactor[IO](config.driver, config.url, config.user, config.password, ce, te)
    } yield xa
  }

  def init(xa: HikariTransactor[IO]): IO[Unit] =
    xa.configure(ds => init(ds.getConnection()))

  def init(connection: Connection): IO[Unit] =
    IO {
      Try {
        val liquibase = new Liquibase(
          "db_changelog.xml",
          new ClassLoaderResourceAccessor(),
          new JdbcConnection(connection)
        )

        liquibase.update(Option.empty[String].orNull)
      }
    } flatMap {
      case Failure(e) => IO.raiseError(e)
      case Success(_) => IO.unit
    }
}
