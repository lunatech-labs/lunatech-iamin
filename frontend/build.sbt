enablePlugins(ScalaJSPlugin)
enablePlugins(JSDependenciesPlugin)
enablePlugins(ScalaJSBundlerPlugin)
enablePlugins(WorkbenchPlugin)

name := "iamin"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % Versions.scalajsDom,
  "com.github.japgolly.scalajs-react" %%% "core" % Versions.scalajsReact,
  "com.github.japgolly.scalajs-react" %%% "extra" % Versions.scalajsReact,
  "com.github.japgolly.scalajs-react" %%% "test" % Versions.scalajsReact,
  "io.suzaku" %%% "diode" % Versions.diode,
  "io.suzaku" %%% "diode-react" % Versions.diodeReact,
  "io.suzaku" %%% "boopickle" % "1.2.6",
  "com.github.japgolly.scalacss" %%% "ext-react" % "0.5.3",
  "com.lihaoyi" %%% "autowire" % "0.2.6",
  "com.lihaoyi" %%% "scalatags" % "0.7.0",
  "be.doeraene" %%% "scalajs-jquery" % "0.9.5"
)

Compile / npmDependencies ++= Seq(
  "react" -> Versions.react,
  "react-dom" -> Versions.react
)

npmDependencies in Compile ++= Seq(
  "jquery" -> "2.1.3",
  "bootstrap" -> "3.3.6"
)

scalaJSUseMainModuleInitializer := true

skip in packageJSDependencies := false

jsDependencies ++= Seq(
  "org.webjars.bower" % "react" % Versions.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
  "org.webjars.bower" % "react" % Versions.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
  "org.webjars" % "jquery" % Versions.jQuery / "jquery.js" minified "jquery.min.js",
  "org.webjars" % "bootstrap" % Versions.bootstrap / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
)
dependencyOverrides += "org.webjars.npm" % "js-tokens" % Versions.jsTokens
workbenchDefaultRootObject := Some(("target/scala-2.12/classes/index.html", "target/scala-2.12/"))

