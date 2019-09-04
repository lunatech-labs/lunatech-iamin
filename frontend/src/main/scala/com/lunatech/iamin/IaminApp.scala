package com.lunatech.iamin

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object IaminApp {

  case class AppState(user: String)

  val component = ScalaComponent.builder[Unit]("Iamin")
    .initialState(AppState("tata"))
    .renderBackend[AppBackend]
    .build

  class AppBackend($: BackendScope[Unit, AppState]) {

    def updateUser() = Callback.future {
      val userId = "7MAEm82r1g0XNZRo"
      IaminAPI.fetchUser(userId) map { user => $.setState(AppState(user.name))}
    }

    def render(s: AppState) = {
      updateUser().logResult.runNow()
      <.div(<.p(s.user))
    }
  }
}
