package com.lunatech.iamin.rest

import com.typesafe.config.ConfigFactory

object ApplicationConfig {
  private val config = ConfigFactory.load()

  private val server = config.getConfig("server")

  val host = server.getString("host")
  val port = server.getInt("port")
}
