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
  "com.lihaoyi" %%% "scalatags" % "0.7.0"
)

Compile / npmDependencies ++= Seq(
  "react" -> "16.7.0",
  "react-dom" -> "16.7.0")

scalaJSUseMainModuleInitializer := true

skip in packageJSDependencies := false

jsDependencies ++= Seq(
  "org.webjars" % "jquery" % Versions.jQuery / "jquery.js" minified "jquery.min.js",
  "org.webjars" % "bootstrap" % Versions.bootstrap / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js"
)

jsDependencies ++= Seq(
  "org.webjars.npm" % "react" % Versions.react
    /        "umd/react.development.js"
    minified "umd/react.production.min.js"
    commonJSName "React",
  "org.webjars.npm" % "react-dom" % Versions.react
    /         "umd/react-dom.development.js"
    minified  "umd/react-dom.production.min.js"
    dependsOn "umd/react.development.js"
    commonJSName "ReactDOM",
  "org.webjars.npm" % "react-dom" % Versions.react
    /         "umd/react-dom-server.browser.development.js"
    minified  "umd/react-dom-server.browser.production.min.js"
    dependsOn "umd/react-dom.development.js"
    commonJSName "ReactDOMServer")

dependencyOverrides += "org.webjars.npm" % "js-tokens" % Versions.jsTokens
workbenchDefaultRootObject := Some(("target/scala-2.12/classes/index.html", "target/scala-2.12/"))

