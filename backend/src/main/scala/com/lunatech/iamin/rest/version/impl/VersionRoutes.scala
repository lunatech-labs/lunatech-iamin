package com.lunatech.iamin.rest.version.impl

import cats.effect.{Async, IO}
import com.lunatech.iamin.rest.BuildInfo
import com.lunatech.iamin.rest.definitions.VersionResponse
import com.lunatech.iamin.rest.version.{GetVersionResponse, VersionHandler}

class VersionRoutes[F[_] : Async](buildInfo: BuildInfo.type) extends VersionHandler[F] {

  override def getVersion(respond: GetVersionResponse.type)(): F[GetVersionResponse] = {
    val io = IO.pure(respond.Ok(VersionResponse(buildInfo.version)).asInstanceOf[GetVersionResponse])

    implicitly[Async[F]].liftIO(io)
  }
}
