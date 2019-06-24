package com.lunatech.iamin

import com.lunatech.iamin.utils.Secret
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigReader, loadConfig}

package object config {

  final case class Config(server: ServerConfig, application: ApplicationConfig, database: DatabaseConfig)

  final case class ServerConfig(host: String, port: Int)

  final case class ApplicationConfig(hashids: HashidsConfig)

  final case class HashidsConfig(minLength: Int, secret: Secret[String])

  final case class DatabaseConfig(driver: String, url: String, user: String, password: Secret[String])

  object Config {
    implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))
    implicit val secretStringReader: ConfigReader[Secret[String]] = ConfigReader[String].map(s => Secret(s))

    def load: ConfigReader.Result[Config] = loadConfig[Config]
  }
}
