val ChimneyVersion = "0.3.1"
val CirceVersion = "0.11.1"
val DoobieVersion = "0.7.0-M5"
val FlywayVersion = "5.2.4"
val HashidsVersion = "1.0.3"
val Http4sVersion = "0.20.1"
val LogbackVersion = "1.2.3"
val OtjPgEmbeddedVersion = "0.13.1"
val PostgresqlVersion = "42.2.5"
val PureConfigVersion = "0.11.0"
val ScalaCheckVersion = "1.14.0"
val ScalaTestVersion = "3.0.7"
val SwaggerUiVersion = "3.22.1"
val TsecVersion = "0.1.0"
val TypesafeConfigVersion = "1.3.4"
val WebjarsLocatorVersion = "0.36"
val ZioVersion = "1.0-RC4"

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging)
  .enablePlugins(DockerPlugin)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    organization := "com.lunatech",
    name := "lunatech-iamin",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",

    fork in run := true,

    maintainer in Docker := "Lunatech Labs <lunatech@lunatech.com>",
    dockerBaseImage := "openjdk:8-jre-slim",

    scalacOptions ++= Seq(
      "-language:higherKinds",
      "-Ypartial-unification"
    ),

    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "com.typesafe" % "config" % TypesafeConfigVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-generic-extras" % CirceVersion,
      "io.circe" %% "circe-java8" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "io.github.jmcardon" %% "tsec-http4s" % TsecVersion,
      "io.github.jmcardon" %% "tsec-jwt-mac" % TsecVersion,
      "io.scalaland" %% "chimney" % ChimneyVersion,
      "org.flywaydb" %  "flyway-core" % FlywayVersion,
      "org.hashids" % "hashids" % HashidsVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.postgresql" % "postgresql" % PostgresqlVersion,
      "org.scalaz" %% "scalaz-zio" % ZioVersion,
      "org.scalaz" %% "scalaz-zio-interop-cats" % ZioVersion,
      "org.tpolecat" %% "doobie-core" % DoobieVersion,
      "org.tpolecat" %% "doobie-hikari" % DoobieVersion,
      "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
      "org.webjars" % "swagger-ui" % SwaggerUiVersion,
      "org.webjars" % "webjars-locator" % WebjarsLocatorVersion,

      "org.scalacheck" %% "scalacheck" % ScalaCheckVersion % Test,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "org.tpolecat" %% "doobie-scalatest" % DoobieVersion % Test,
    ),

    buildInfoPackage := "com.lunatech.iamin.utils",
    buildInfoObject := "_BuildInfo",

    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0"),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.10"),

    guardrailTasks in Compile := List(
      ScalaServer(
        file(((Compile / resourceDirectory).value / "api.yaml").toPath.toString),
        pkg = "com.lunatech.iamin.endpoints",
        framework = "http4s",
        tracing = false,
        imports = List(
          "com.lunatech.iamin.utils.guardrailhacks._"
        )
      )
    )
  )

(compile in Compile) := ((compile in Compile) dependsOn dependencyUpdates).value

// Wartremover specifics
//wartremoverErrors ++= Warts.unsafe
//wartremoverErrors --= Seq(
//  wartremover.Wart.DefaultArguments
//)
//
//wartremoverWarnings ++= Warts.all
//wartremoverWarnings --= Seq(
//  wartremover.Wart.DefaultArguments
//)
//
//wartremoverExcluded += baseDirectory.value / "src" / "test" / "scala"
//wartremoverExcluded += baseDirectory.value / "src" / "main" / "scala" / "com" / "lunatech" / "iamin" / "database" / "tables" // Slick generated
//wartremoverExcluded += sourceManaged.value