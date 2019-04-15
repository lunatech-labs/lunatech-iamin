package com.lunatech.iamin

import cats.effect.IO

package object config {

  case class ServerConfig(host: String, port: Int)

  case class DatabaseConfig(driver: String, url: String, user: String, password: String)

  case class Config(server: ServerConfig, database: DatabaseConfig)

  object Config {
    import com.typesafe.config.ConfigFactory
    import pureconfig._
    import pureconfig.error.ConfigReaderException
    import pureconfig.generic.auto._
    import pureconfig.generic.ProductHint

    implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

    def load(file: String): IO[Config] = IO {
      loadConfig[Config](ConfigFactory.load(file))
    } flatMap {
      case Left(e) => IO.raiseError(new ConfigReaderException[Config](e))
      case Right(config) => IO.pure(config)
    }
  }
}
