package com.lunatech.iamin.utils

import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.sql.DriverManager

import com.lunatech.iamin.config.DatabaseConfig
import com.typesafe.scalalogging.StrictLogging
import liquibase.Liquibase
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor

object DatabaseMigrator extends StrictLogging {

  /**
    * Applies all Liquibase migrations to the database specified in the config.
    * @param config Configuration used to access the database
    * @param dryRun Indicates it the migrations should be printed instead of being really applied
    */
  def applyMigrations(config: DatabaseConfig, dryRun: Boolean): Unit = {

    val connection = DriverManager.getConnection(
      config.properties.url,
      config.properties.user,
      config.properties.password
    )

    applyMigrations(connection, dryRun)
  }

  /**
    * Applies all Liquibase migrations to the specified connection.
    * @param connection Database connection to use
    * @param dryRun Indicates it the migrations should be printed instead of being really applied
    */
  def applyMigrations(connection: java.sql.Connection, dryRun: Boolean): Unit = {

    logger.debug(s"Applying Liquibase migrations...")

    val liquibase = new Liquibase(
      "db_changelog.xml",
      new ClassLoaderResourceAccessor(),
      new JdbcConnection(connection)
    )

    try {

      if (dryRun) {
        liquibase.update(Option.empty[String].orNull, new OutputStreamWriter(System.out, StandardCharsets.UTF_8))
      } else {
        // For real
        liquibase.update(Option.empty[String].orNull)
      }

      logger.debug("Applying Liquibase migrations complete!")
    } catch {
      case e: LiquibaseException =>
        logger.error(s"Applying Liquibase migrations failed: ${e.getMessage}", e)
        throw e
    }
  }
}
