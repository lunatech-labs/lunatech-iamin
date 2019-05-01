package com.lunatech.iamin.endpoints

import cats.effect.{ContextShift, Sync}
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.{HttpRoutes, StaticFile, Uri}
import org.webjars.WebJarAssetLocator

import scala.concurrent.ExecutionContext

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class SwaggerResource[F[_] : Sync](blockingEc: ExecutionContext)
                                          (implicit val cs: ContextShift[F]) extends Http4sDsl[F] {

  private val swaggerUiVersion =
    Option(new WebJarAssetLocator().getWebJars.get("swagger-ui"))
      .fold[String](throw new RuntimeException("Could not detect swagger-ui version"))(identity)
  private val swaggerUiResources = s"/META-INF/resources/webjars/swagger-ui/$swaggerUiVersion"

  private val swaggerUiPath = Path("swagger-ui")

  def routes(): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> `swaggerUiPath` => PermanentRedirect(Location(Uri.uri("swagger-ui/index.html")))
    case req @ GET -> path if path.startsWith(swaggerUiPath) => {
      val file = "/" + path.toList.drop(swaggerUiPath.toList.size).mkString("/")

      (file match {
        case "/index.html" =>
          StaticFile.fromResource("/swagger-ui/index.html", blockingEc, req.some)
        case "/api.yaml" =>
          StaticFile.fromResource("/api.yaml", blockingEc, req.some)
        case res =>
          StaticFile.fromResource(swaggerUiResources + res, blockingEc, req.some)
      }).getOrElseF(NotFound())
    }
  }
}
