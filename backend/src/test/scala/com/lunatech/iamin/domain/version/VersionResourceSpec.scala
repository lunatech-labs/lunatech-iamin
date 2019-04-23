package com.lunatech.iamin.domain.version

import cats.effect.IO
import com.lunatech.iamin.rest.definitions.VersionResponse
import com.lunatech.iamin.rest.version.VersionResource
import com.lunatech.iamin.utils.BuildInfo
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, Method, Request, Uri}
import org.scalatest.{FreeSpec, Matchers}

class VersionResourceSpec extends FreeSpec with Matchers {

  private val buildInfo: BuildInfo = BuildInfo("iamin", "test-version", "-1", "-1")

  private val resource = new VersionResource[IO].routes(new VersionHandlerImpl[IO](buildInfo))

  implicit private val versionResponseDec: EntityDecoder[IO, VersionResponse] = jsonOf

  "version resource" - {

    "GET /version should" - {
      "return the correct version" in {
        (for {
          getVersionResponse <- resource.orNotFound(Request[IO](Method.GET, Uri.uri("/version")))
          version            <- getVersionResponse.as[VersionResponse]
        } yield {
          version shouldBe VersionResponse(buildInfo.version)
        }).unsafeRunSync()
      }
    }
  }
}
