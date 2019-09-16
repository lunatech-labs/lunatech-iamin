enablePlugins(ScalaJSPlugin)
enablePlugins(JSDependenciesPlugin)
enablePlugins(ScalaJSBundlerPlugin)
enablePlugins(WorkbenchPlugin)
enablePlugins(SbtWeb)
enablePlugins(ScalaJSWeb)

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
  "org.webjars" % "font-awesome" % "4.3.0-1" % Provided
)

Compile / npmDependencies ++= Seq(
  "react" -> "16.7.0",
  "react-dom" -> "16.7.0")

scalaJSUseMainModuleInitializer := true

skip in packageJSDependencies := false

jsDependencies ++= Seq(
//  "org.webjars.bower" % "react" % Versions.react / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React",
//  "org.webjars.bower" % "react" % Versions.react / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM",
  "org.webjars" % "jquery" % Versions.jQuery / "jquery.js" minified "jquery.min.js",
  "org.webjars" % "bootstrap" % Versions.bootstrap / "bootstrap.js" minified "bootstrap.min.js" dependsOn "jquery.js",
  "org.webjars" % "chartjs" % Versions.chartjs / "Chart.js" minified "Chart.min.js",
  "org.webjars" % "log4javascript" % Versions.log4js / "js/log4javascript_uncompressed.js" minified "js/log4javascript.js"
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

compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value

pipelineStages in Assets := Seq(scalaJSPipeline)
pipelineStages := Seq(digest, gzip)
LessKeys.compress in Assets := true
