package com.lunatech.iamin.rest

import com.typesafe.config.ConfigFactory

object ApplicationConfig {
  private val config = ConfigFactory.load()

  private val server = config.getConfig("server")

  @deprecated(message = "Use com.lunatech.iamin.config.Config.server.host instead")
  val host = server.getString("host")

  @deprecated(message = "Use com.lunatech.iamin.config.Config.server.port instead")
  val port = server.getInt("port")
}
