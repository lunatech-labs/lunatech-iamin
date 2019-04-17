package com.lunatech.iamin.utils

import java.sql.DriverManager

import com.lunatech.iamin.config.DatabaseConfig
import com.typesafe.scalalogging.StrictLogging
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor

import scala.util.Try

object DatabaseMigrator extends StrictLogging {

  /**
    * Applies all Liquibase migrations to the database specified in the config.
    * @param config Configuration used to access the database
    */
  def applyMigrations(config: DatabaseConfig): Either[Throwable, Unit] = {

    val connection = DriverManager.getConnection(
      config.properties.url,
      config.properties.user,
      config.properties.password
    )

    applyMigrations(connection)
  }

  /**
    * Applies all Liquibase migrations to the specified connection.
    * @param connection Database connection to use
    */
  def applyMigrations(connection: java.sql.Connection): Either[Throwable, Unit] = {

    logger.debug(s"Applying Liquibase migrations...")

    Try {
      val liquibase = new Liquibase(
        "db_changelog.xml",
        new ClassLoaderResourceAccessor(),
        new JdbcConnection(connection)
      )

      liquibase.update(Option.empty[String].orNull)

      logger.debug("Applying Liquibase migrations complete!")
    }.toEither.left.map { t =>
      logger.error(s"Applying liquibase migration failed: ${t.getMessage}", t)
      t
    }
  }
}
