package com.lunatech.iamin

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object IaminApp {

  case class AppState(user: String)

  val component = ScalaComponent.builder[Unit]("Iamin")
    .initialState(AppState(""))
    .renderBackend[AppBackend]
    .build

  class AppBackend($: BackendScope[Unit, AppState]) {

    def updateUser() = Callback.future {
      val userId = "BVmN9k2xkg3R0XOL"
      IaminAPI.fetchUser(userId) map { user => $.setState(AppState(user.name))}
    }

    def render(s: AppState) = {
      <.div(
        <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width",
                ^.onClick --> updateUser,
                "Get user"
        ),
        <.p(s.user))
    }
  }
}
