enablePlugins(ScalaJSPlugin)
enablePlugins(JSDependenciesPlugin)

name := "iamin"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % Versions.scalajs,
  "com.github.japgolly.scalajs-react" %%% "core" % Versions.scalajsReact,
  "com.github.japgolly.scalajs-react" %%% "extra" % Versions.scalajsReact,
  "com.github.japgolly.scalajs-react" %%% "test" % Versions.scalajsReact,
  "io.suzaku" %%% "diode" % Versions.diode
)

scalaJSUseMainModuleInitializer := true

skip in packageJSDependencies := false

jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv

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
