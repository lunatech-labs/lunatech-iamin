package com.lunatech.iamin.config

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import com.typesafe.config.ConfigFactory

case class Config(
                   server: ServerConfig,
                   database: DatabaseConfig
                 )

object Config {
  lazy val rootConfig: Config = ConfigFactory.load().as[Config]

  lazy val server: ServerConfig = rootConfig.server

  lazy val database: DatabaseConfig = rootConfig.database
}