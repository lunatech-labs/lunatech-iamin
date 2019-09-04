package com.lunatech.iamin

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object IaminApp {

  case class AppState(user: String)

  val component = ScalaComponent.builder[Unit]("Iamin")
    .initialState(AppState("toto"))
    .renderBackend[AppBackend]
    .build

  class AppBackend($: BackendScope[Unit, AppState]) {

//    val userState = $.zoom(_.user)((s, x) => s.copy(user = x))

    def render(s: AppState) = <.div(<.p(s.user))
  }
}
