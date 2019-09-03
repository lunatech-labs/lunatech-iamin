enablePlugins(ScalaJSPlugin)

name := "iamin"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.github.japgolly.scalajs-react" %%% "core" % "1.4.0",
  "com.github.japgolly.scalajs-react" %%% "extra" % "1.4.0",
  "com.github.japgolly.scalajs-react" %%% "test" % "1.4.0",
  "org.scala-js" %%% "scalajs-dom" % "0.9.7"
)

scalaJSUseMainModuleInitializer := true


jsDependencies ++= Seq(

  "org.webjars.npm" % "react" % "16.7.0"
    /        "umd/react.development.js"
    minified "umd/react.production.min.js"
    commonJSName "React",

  "org.webjars.npm" % "react-dom" % "16.7.0"
    /         "umd/react-dom.development.js"
    minified  "umd/react-dom.production.min.js"
    dependsOn "umd/react.development.js"
    commonJSName "ReactDOM",

  "org.webjars.npm" % "react-dom" % "16.7.0"
    /         "umd/react-dom-server.browser.development.js"
    minified  "umd/react-dom-server.browser.production.min.js"
    dependsOn "umd/react-dom.development.js"
    commonJSName "ReactDOMServer")

dependencyOverrides += "org.webjars.npm" % "js-tokens" % "3.0.2"
