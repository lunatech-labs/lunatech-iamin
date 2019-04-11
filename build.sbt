val CirceVersion = "0.11.1"
val FicusVersion = "1.4.5"
val Http4sVersion = "0.19.0"
val LiquibaseVersion = "3.6.3"
val LogbackVersion = "1.2.3"
val OtjPgEmbeddedVersion = "0.13.1"
val PostgresqlVersion = "42.2.5"
val SlickPgVersion = "0.17.0"
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
      "com.github.tminglei" %% "slick-pg" % SlickPgVersion,
      "com.iheart" %% "ficus" % FicusVersion,
      "com.opentable.components" % "otj-pg-embedded" % OtjPgEmbeddedVersion,
      "com.typesafe" % "config" % TypesafeConfigVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % TypsafeLoggingVersion,
      "com.typesafe.slick" %% "slick" % SlickVersion,
      "com.typesafe.slick" %% "slick-codegen" % SlickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.liquibase" % "liquibase-core" % LiquibaseVersion,
      "org.postgresql" % "postgresql" % PostgresqlVersion,

      "org.specs2" %% "specs2-core" % Specs2Version % Test,
    ) ++ Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-java8",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % CirceVersion),

    buildInfoPackage := "com.lunatech.iamin.rest",

    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),

    guardrailTasks in Compile := List(
      ScalaServer(file("documentation/api.yaml"), pkg = "com.lunatech.iamin.rest", framework = "http4s", tracing = false)
    )
  )
