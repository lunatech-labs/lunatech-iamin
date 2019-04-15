package com.lunatech.iamin.rest

import cats.effect.IO
import org.http4s.implicits._
import com.lunatech.iamin.rest.version.VersionResource
import com.lunatech.iamin.rest.version.impl.VersionHandler
import org.http4s._
import org.specs2.matcher.MatchResult

object BuildInfo {
  def version: String = "test-version"
}

class VersionRouteSpec extends org.specs2.mutable.Specification {

  "Version route" >> {
    "return 200" >> {
      uriReturns200()
    }
    "return the current app's version" >> {
      uriReturnsVersion()
    }
  }

  private[this] val retVersion: Response[IO] = {
    val getHW = Request[IO](Method.GET, Uri.uri("/version"))
    val version = VersionHandler.impl[IO](BuildInfo)
    new VersionResource[IO]().routes(version).orNotFound(getHW).unsafeRunSync()
  }

  private[this] def uriReturns200(): MatchResult[Status] =
    retVersion.status must beEqualTo(Status.Ok)

  private[this] def uriReturnsVersion(): MatchResult[String] =
    retVersion.as[String].unsafeRunSync() must beEqualTo(s"""{"version":"${BuildInfo.version}"}""")
}