package com.lunatech.iamin.config

case class DatabaseConfig(
                           dataSourceClass: String,
                           properties: DatabasePropertiesConfig,
                           connectionPool: String,
                           maxConnections: Int,
                           minConnections: Int,
                           numThreads: Int,
                           queueSize: Int
                         )

case class DatabasePropertiesConfig(
                                   host: String,
                                   port: Int,
                                   databaseName: String,
                                   user: String,
                                   password: String,
                                   driver: String,
                                   url: String
                                   )
