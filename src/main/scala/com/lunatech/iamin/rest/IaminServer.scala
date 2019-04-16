package com.lunatech.iamin.rest


import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.lunatech.iamin.config.ServerConfig
import com.lunatech.iamin.rest.version.VersionResource
import com.lunatech.iamin.rest.version.impl.VersionHandler
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

class IaminServer(serverConfig: ServerConfig) {

  def stream[F[_] : ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)
      versionAlg = VersionHandler.impl[F]

      // Combine Service Routes into an HttpApp
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        // TODO: get rid of all the endpoints from the http4s.g8 template
        IaminRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
          IaminRoutes.jokeRoutes[F](jokeAlg) <+>
          new VersionResource().routes(versionAlg)
        ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger(logHeaders = true, logBody = true)(httpApp)


      exitCode <- BlazeServerBuilder[F]
        // TODO: don't use Config object, but something like Stream.eval(Config.load())
        .bindHttp(serverConfig.port, serverConfig.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
