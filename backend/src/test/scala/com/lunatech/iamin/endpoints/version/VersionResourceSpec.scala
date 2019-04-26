package com.lunatech.iamin.endpoints.version

import cats.effect.IO
import cats.implicits._
import com.lunatech.iamin.endpoints.VersionHandlerImpl
import com.lunatech.iamin.endpoints.definitions.VersionResponseJson
import com.lunatech.iamin.utils.{BuildInfo, Http4sTestSupport}
import org.http4s.implicits._
import org.http4s.{Method, Request, Status, Uri}
import org.scalatest.{FreeSpec, ParallelTestExecution}

class VersionResourceSpec extends FreeSpec with Http4sTestSupport with ParallelTestExecution {

  private val buildInfo = BuildInfo("iamin", "test-version", "-1", "-1")
  private val resource = new VersionResource[IO].routes(new VersionHandlerImpl[IO](buildInfo))

  "version resource" - {
    "GET /version should" - {
      "return correct version" in {
        check(
          resource.orNotFound(Request(Method.GET, Uri.uri("/version"))),
          Status.Ok,
          VersionResponseJson(buildInfo.version).some
        )
      }
    }
  }
}
