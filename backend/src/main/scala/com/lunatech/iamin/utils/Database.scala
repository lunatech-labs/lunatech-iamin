package com.lunatech.iamin.utils

import com.lunatech.iamin.config.DatabaseConfig
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import scalaz.zio.interop.catz._
import scalaz.zio.{Managed, Reservation, Task, ZIO}

import scala.concurrent.ExecutionContext

object Database {

  def runMigrations(config: DatabaseConfig): Task[Unit] = ZIO.effect {
    val flyway = Flyway
      .configure.dataSource(config.url, config.user, config.password.value)
      .load

    flyway.migrate()
  }.unit

  def mkTransactor(
    config: DatabaseConfig,
    connectEc: ExecutionContext,
    transactEc: ExecutionContext
  ): Managed[Throwable, Transactor[Task]] = {
    val transactor = HikariTransactor.newHikariTransactor[Task](
      config.driver,
      config.url,
      config.user,
      config.password.value,
      connectEc,
      transactEc
    )

    val resource = transactor.allocated
      .map { case (xa, cleanupM) => Reservation(ZIO.succeed(xa), cleanupM.orDie) }
      .uninterruptible

    Managed(resource)
  }
}
