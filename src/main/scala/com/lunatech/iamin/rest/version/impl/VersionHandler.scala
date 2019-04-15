package com.lunatech.iamin.rest.version.impl

import cats.Applicative
import com.lunatech.iamin.rest.BuildInfo
import com.lunatech.iamin.rest.definitions.VersionResponse
import com.lunatech.iamin.rest.version.{GetVersionResponse, VersionHandler => VersionHandlerApi}

object VersionHandler {
  def impl[F[_] : Applicative](versionProvider: BuildInfo.type): VersionHandlerApi[F] = new VersionHandlerApi[F] {
    override def getVersion(respond: GetVersionResponse.type)(): F[GetVersionResponse] =
      implicitly[Applicative[F]].pure(respond.Ok(VersionResponse(versionProvider.version)))
  }
}
