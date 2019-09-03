package com.lunatech.iamin

import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom.document
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object App {

  val NoArgs = ScalaComponent.static("No args")(<.div("Hello!"))

  def main(args: Array[String]): Unit = NoArgs().renderIntoDOM(document.getElementById("mount"))

}
