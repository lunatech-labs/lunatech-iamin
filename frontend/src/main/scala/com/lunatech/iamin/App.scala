package com.lunatech.iamin

import org.scalajs.dom

object App {

  def main(args: Array[String]): Unit = {
    IaminApp.component().renderIntoDOM(dom.document.getElementById("mount"))
  }
}
