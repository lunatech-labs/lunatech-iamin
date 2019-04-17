import scala.util.Try

val CirceVersion = "0.11.1"
val DoobieVersion = "0.6.0"
val Http4sVersion = "0.19.0"
val LiquibaseVersion = "3.6.3"
val LogbackVersion = "1.2.3"
val OtjPgEmbeddedVersion = "0.13.1"
val PostgresqlVersion = "42.2.5"
val PureConfigVersion = "0.10.2"
val SlickPgVersion = "0.17.2"
val SlickVersion = "3.3.0"
val Specs2Version = "4.5.1"
val TypesafeConfigVersion = "1.3.3"
val TypsafeLoggingVersion = "3.9.2"

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
      "com.typesafe" % "config" % TypesafeConfigVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % TypsafeLoggingVersion,
      "com.typesafe.slick" %% "slick" % SlickVersion,
      "com.typesafe.slick" %% "slick-codegen" % SlickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-java8" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.liquibase" % "liquibase-core" % LiquibaseVersion,
      "org.postgresql" % "postgresql" % PostgresqlVersion,

      "org.specs2" %% "specs2-core" % Specs2Version % Test,
    ),

    buildInfoPackage := "com.lunatech.iamin.rest",

    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0"),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.10"),

    guardrailTasks in Compile := List(
      ScalaServer(file("documentation/api.yaml"), pkg = "com.lunatech.iamin.rest", framework = "http4s", tracing = false)
    )
  )

lazy val generateSlickTables = taskKey[Try[Unit]]("Generate Slick code from Liquibase migrations")
generateSlickTables := {
  val outputDirectory = sourceDirectory.value / "main" / "scala"
  val cp = (Compile / fullClasspath).value
  val log = streams.value.log
  val r = (Compile / runner).value

  r.run("com.lunatech.iamin.database.SlickTableGenerator", cp.files, Array(outputDirectory.getPath), log)
}
