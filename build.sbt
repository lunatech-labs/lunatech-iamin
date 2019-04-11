val CirceVersion = "0.11.1"
val Http4sVersion = "0.19.0"
val Specs2Version = "4.5.1"
val LogbackVersion = "1.2.3"
val TypesafeConfigVersion = "1.3.3"

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

    scalacOptions ++= Seq("-Ypartial-unification"),

    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "test",
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.typesafe" % "config" % TypesafeConfigVersion
    ),

    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-java8",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % CirceVersion),

    buildInfoPackage := "com.lunatech.iamin.rest",

    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),

    guardrailTasks in Compile := List(
      ScalaServer(file("documentation/api/version.yaml"), pkg = "com.lunatech.iamin.rest", framework = "http4s", tracing = false)
    )
  )

