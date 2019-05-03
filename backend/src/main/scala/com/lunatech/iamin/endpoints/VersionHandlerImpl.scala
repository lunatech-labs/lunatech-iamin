package com.lunatech.iamin.endpoints

import cats.effect.{Async, IO}
import com.lunatech.iamin.endpoints.definitions.VersionResponseJson
import com.lunatech.iamin.endpoints.version.{GetVersionResponse, VersionHandler}
import com.lunatech.iamin.utils.BuildInfo

class VersionHandlerImpl[F[_] : Async](buildInfo: BuildInfo) extends VersionHandler[F] {

  override def getVersion(respond: GetVersionResponse.type)(): F[GetVersionResponse] = {
    IO.pure(respond.Ok(VersionResponseJson(buildInfo.version)).asInstanceOf[GetVersionResponse]).to[F]
  }
}
