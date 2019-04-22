package com.lunatech.iamin.rest


import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.lunatech.iamin.config.Config
import com.lunatech.iamin.database.Profile.api.Database
import com.lunatech.iamin.domain.users.{DatabaseUsersRepository, UsersHandlerImpl}
import com.lunatech.iamin.domain.version.VersionHandlerImpl
import com.lunatech.iamin.rest.users.UsersResource
import com.lunatech.iamin.rest.version.VersionResource
import com.lunatech.iamin.utils.BuildInfo
import fs2.Stream
import org.hashids.Hashids
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

class IaminServer(config: Config, database: Database) {

  @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
  def stream[F[_] : ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

    for {
      client <- BlazeClientBuilder[F](global).stream
      usersRepository = new DatabaseUsersRepository(database)
      hashids = new Hashids(config.application.hashids.secret, config.application.hashids.minLength)

      httpApp = (
        new VersionResource().routes(new VersionHandlerImpl[F](BuildInfo)) <+>
        new UsersResource().routes(new UsersHandlerImpl[F](hashids, usersRepository))
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger(logHeaders = true, logBody = true)(httpApp)


      exitCode <- BlazeServerBuilder[F]
        // TODO: don't use Config object, but something like Stream.eval(Config.load())
        .bindHttp(config.server.port, config.server.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}