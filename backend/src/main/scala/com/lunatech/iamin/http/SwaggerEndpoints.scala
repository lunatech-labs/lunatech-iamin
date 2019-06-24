package com.lunatech.iamin.http

import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.{HttpRoutes, StaticFile, Uri}
import org.webjars.WebJarAssetLocator
import scalaz.zio.interop.catz._
import scalaz.zio.TaskR

import scala.concurrent.ExecutionContext

final class SwaggerEndpoints[R](blockingEc: ExecutionContext) {

  type SwaggerEndpointTask[A] = TaskR[R, A]

  private val dsl: Http4sDsl[SwaggerEndpointTask] = Http4sDsl[SwaggerEndpointTask]

  import dsl._

  private val swaggerUiVersion =
    Option(new WebJarAssetLocator().getWebJars.get("swagger-ui"))
      .fold[String](throw new RuntimeException("Could not detect swagger-ui version"))(identity)
  private val swaggerUiResources = s"/META-INF/resources/webjars/swagger-ui/$swaggerUiVersion"
  private val swaggerUiPath = Path("swagger-ui")

  def routes: HttpRoutes[SwaggerEndpointTask] = HttpRoutes.of[SwaggerEndpointTask] {
    case GET -> `swaggerUiPath` => PermanentRedirect(Location(Uri.uri("swagger-ui/index.html")))
    case req @ GET -> path if path.startsWith(swaggerUiPath) =>
      val file = "/" + path.toList.drop(swaggerUiPath.toList.size).mkString("/")

      (file match {
        case "/index.html" => StaticFile.fromResource("/swagger-ui/index.html", blockingEc, Some(req))
        case "/api.yaml" => StaticFile.fromResource("/api.yaml", blockingEc, Some(req))
        case resource => StaticFile.fromResource(swaggerUiResources + resource, blockingEc, Some(req))
      }).getOrElseF(NotFound())
  }
}
