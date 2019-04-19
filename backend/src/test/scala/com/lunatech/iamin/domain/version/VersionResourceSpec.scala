package com.lunatech.iamin.domain.version

import cats.effect.IO
import com.lunatech.iamin.rest.definitions.VersionResponse
import com.lunatech.iamin.rest.version.VersionResource
import com.lunatech.iamin.utils.BuildInfo
import io.circe.Json
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.{Method, Request, Response, Status, Uri}
import org.scalatest.{FreeSpec, Matchers}

class VersionResourceSpec extends FreeSpec with Matchers {

  private val buildInfo: BuildInfo = BuildInfo("iamin", "test-version", "-1", "-1")

  private val resource = new VersionResource[IO].routes(new VersionHandlerImpl[IO](buildInfo))

  "version resource" - {

    "GET /version should" - {
      "return the correct version" in {
        val response = serve(Request[IO](Method.GET, Uri.uri("/version")))

        response.status shouldBe Status.Ok
        response.as[Json].unsafeRunSync() shouldBe VersionResponse("test-version").asJson
      }
    }
  }

  private def serve(request: Request[IO]): Response[IO] = {
    resource.orNotFound(request).unsafeRunSync()
  }
}
