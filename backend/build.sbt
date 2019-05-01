import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.{Failure, Success, Try}

val ChimneyVersion = "0.3.1"
val CirceVersion = "0.11.1"
val HashidsVersion = "1.0.3"
val Http4sVersion = "0.20.0"
val LiquibaseVersion = "3.6.3"
val LogbackVersion = "1.2.3"
val OtjPgEmbeddedVersion = "0.13.1"
val PostgresqlVersion = "42.2.5"
val PureConfigVersion = "0.10.2"
val ScalaCheckVersion = "1.14.0"
val ScalaTestVersion = "3.0.7"
val SlickPgVersion = "0.17.2"
val SlickVersion = "3.3.0"
val SwaggerUiVersion = "3.22.0"
val WebjarsLocatorVersion = "0.36"

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
      "com.github.pureconfig" % "pureconfig_2.12" % PureConfigVersion,
      "com.github.tminglei" %% "slick-pg" % SlickPgVersion,
      "com.opentable.components" % "otj-pg-embedded" % OtjPgEmbeddedVersion,
      "com.typesafe.slick" %% "slick" % SlickVersion,
      "com.typesafe.slick" %% "slick-codegen" % SlickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-generic-extras" % CirceVersion,
      "io.circe" %% "circe-java8" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "io.scalaland" %% "chimney" % ChimneyVersion,
      "org.hashids" % "hashids" % HashidsVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.liquibase" % "liquibase-core" % LiquibaseVersion,
      "org.postgresql" % "postgresql" % PostgresqlVersion,
      "org.webjars" % "swagger-ui" % SwaggerUiVersion,
      "org.webjars" % "webjars-locator" % WebjarsLocatorVersion,

      "org.scalacheck" %% "scalacheck" % ScalaCheckVersion % Test,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
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
          "com.lunatech.iamin.utils.guardrail.hacks._"
        )
      )
    )
  )

(compile in Compile) := ((compile in Compile) dependsOn dependencyUpdates).value

lazy val generateSlickTables = taskKey[Try[Unit]]("Generate Slick code from Liquibase migrations")
generateSlickTables := {
  val outputDirectory = sourceDirectory.value / "main" / "scala"
  val cp = (Compile / fullClasspath).value
  val log = streams.value.log
  val r = (Compile / runner).value

  r.run("com.lunatech.iamin.database.SlickTableGenerator", cp.files, Array(outputDirectory.getPath), log)
}

lazy val createLiquibaseMigration = inputKey[Try[File]]("create a Liquibase migration file")
createLiquibaseMigration := {
  import complete.DefaultParsers._

  val description = spaceDelimited("<arg>").parsed.map(_.replaceAll(" ", "_").toLowerCase).mkString("_")

  if (description.isEmpty) {
    Failure(new IllegalArgumentException("migration description cannot be empty"))
  } else {

    val date = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString
    val filename = s"${date}_$description"
    val username = Option(System.getProperty("user.name")).getOrElse("sbt")
    val file = (Compile / resourceDirectory).value / "migrations" / s"$filename.xml"
    val content =
      s"""
        |<databaseChangeLog
        |        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        |        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        |        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd">
        |    <changeSet id="$filename" author="$username">
        |
        |        <!-- implement your migration here -->
        |
        |    </changeSet>
        |</databaseChangeLog>
      """.stripMargin
    val log = streams.value.log

    IO.write(file, content, StandardCharsets.UTF_8)

    log.info(s"Create new Liquibase migration file at: ${file.toString}")

    Success(file)
  }
}

// Wartremover specifics
wartremoverErrors ++= Warts.unsafe
wartremoverErrors --= Seq(
  wartremover.Wart.DefaultArguments
)

wartremoverWarnings ++= Warts.all
wartremoverWarnings --= Seq(
  wartremover.Wart.DefaultArguments
)

wartremoverExcluded += baseDirectory.value / "src" / "test" / "scala"
wartremoverExcluded += baseDirectory.value / "src" / "main" / "scala" / "com" / "lunatech" / "iamin" / "database" / "tables" // Slick generated
wartremoverExcluded += sourceManaged.value