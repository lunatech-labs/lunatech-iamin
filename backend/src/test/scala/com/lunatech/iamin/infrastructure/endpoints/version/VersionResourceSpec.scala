package com.lunatech.iamin.infrastructure.endpoints.version

import cats.effect.IO
import com.lunatech.iamin.endpoints.definitions.VersionResponse
import com.lunatech.iamin.endpoints.version.{VersionHandlerImpl, VersionResource}
import com.lunatech.iamin.utils.BuildInfo
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, Method, Request, Uri}
import org.scalatest.{FreeSpec, Matchers}

class VersionResourceSpec extends FreeSpec with Matchers {

  private val buildInfo = BuildInfo("iamin", "test-version", "-1", "-1")
  private val resource = new VersionResource[IO].routes(new VersionHandlerImpl[IO](buildInfo))

  private implicit val versionRespionseDec: EntityDecoder[IO, VersionResponse] = jsonOf

  "version resource" - {
    "GET /version should" - {
      "return correct version" in {
        (for {
          getVersionRequest  <- IO.pure(Request[IO](Method.GET, Uri.uri("/version")))
          getVersionResponse <- resource.orNotFound(getVersionRequest)
          version <- getVersionResponse.as[VersionResponse]
        } yield {
          version shouldBe VersionResponse(buildInfo.version)
        }).unsafeRunSync()
      }
    }
  }
}
