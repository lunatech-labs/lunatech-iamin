val FicusVersion = "1.4.5"
val Http4sVersion = "0.18.23"
val LiquibaseVersion = "3.6.3"
val LogbackVersion = "1.2.3"
val OtjPgEmbeddedVersion = "0.13.1"
val PostgresqlVersion = "42.2.5"
val Specs2Version = "4.5.1"
val TypesafeConfigVersion = "1.3.3"
val TypsafeLoggingVersion = "3.9.2"

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    organization := "com.lunatech",
    name := "lunatech-iamin",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",

    fork in run := true,

    maintainer in Docker := "Lunatech Labs <lunatech@lunatech.com>",
    dockerBaseImage := "openjdk:8-jre-slim",

    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.iheart" %% "ficus" % FicusVersion,
      "com.opentable.components" % "otj-pg-embedded" % OtjPgEmbeddedVersion,
      "com.typesafe" % "config" % TypesafeConfigVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % TypsafeLoggingVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.liquibase" % "liquibase-core" % LiquibaseVersion,
      "org.postgresql" % "postgresql" % PostgresqlVersion,

      "org.specs2" %% "specs2-core" % Specs2Version % Test,
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")
  )
