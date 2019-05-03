package com.lunatech.iamin

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.error.ConfigReaderException
import pureconfig.generic.auto._
import pureconfig.generic.ProductHint

package object config {

  final case class ServerConfig(host: String, port: Int)

  final case class ApplicationConfig(hashids: HashidsConfig, threadpools: ThreadpoolsConfig)

  final case class ThreadpoolsConfig(blockingFileThreadpool: FixedThreadpoolConfig)

  final case class FixedThreadpoolConfig(fixedSize: Int)

  final case class HashidsConfig(minLength: Int, secret: String)

  final case class DatabaseConfig(driver: String, url: String, user: String, password: String)

  final case class Config(server: ServerConfig, database: DatabaseConfig, application: ApplicationConfig)

  object Config {

    implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

    def load(): IO[Config] = IO {
      loadConfig[Config](ConfigFactory.load())
    } flatMap {
      case Left(e) => IO.raiseError[Config](new ConfigReaderException[Config](e))
      case Right(config) => IO.pure(config)
    }
  }
}
