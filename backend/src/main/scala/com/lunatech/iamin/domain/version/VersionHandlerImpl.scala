package com.lunatech.iamin.domain.version

import cats.effect.{Async, IO}
import com.lunatech.iamin.rest.definitions.VersionResponse
import com.lunatech.iamin.rest.version.{GetVersionResponse, VersionHandler}
import com.lunatech.iamin.utils.BuildInfo

class VersionHandlerImpl[F[_] : Async](buildInfo: BuildInfo) extends VersionHandler[F] {

  override def getVersion(respond: GetVersionResponse.type)(): F[GetVersionResponse] = {
    val io = IO.pure(respond.Ok(VersionResponse(buildInfo.version)).asInstanceOf[GetVersionResponse])

    implicitly[Async[F]].liftIO(io)
  }
}