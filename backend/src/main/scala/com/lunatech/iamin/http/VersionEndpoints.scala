package com.lunatech.iamin.http

import com.lunatech.iamin.endpoints.definitions.VersionResponseJson
import com.lunatech.iamin.endpoints.version.{GetVersionResponse, VersionHandler, VersionResource}
import com.lunatech.iamin.utils.BuildInfo
import org.http4s.HttpRoutes
import scalaz.zio.interop.catz._
import scalaz.zio.{TaskR, UIO}

final class VersionEndpoints[R] {

  type VersionEndpointTask[A] = TaskR[R, A]

  def routes: HttpRoutes[VersionEndpointTask] = new VersionResource[VersionEndpointTask].routes(new VersionHandlerImpl)

  private class VersionHandlerImpl extends VersionHandler[VersionEndpointTask] {

    override def getVersion(respond: GetVersionResponse.type)(): VersionEndpointTask[GetVersionResponse] = {
      UIO.succeed(respond.Ok(VersionResponseJson(BuildInfo.version)))
    }
  }
}
