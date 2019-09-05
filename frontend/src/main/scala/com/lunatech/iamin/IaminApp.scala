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

    def onChange(e: ReactEventFromInput) = {
      val newValue = e.target.value
      println(newValue)
      $.modState(_.copy(user = newValue))
    }

    def handleSubmit(e: ReactEventFromInput) = {
      e.preventDefaultCB >> postUser(e.target.value)
    }

    def postUser(name: String) = Callback.future {
      println(name)
      IaminAPI.postUser(name).map { user =>
        $.setState(AppState(user.name))
      }
    }

    def render(state: AppState) =
      <.div(
        <.form(^.onSubmit --> postUser(state.user),
          <.input(^.onChange ==> onChange, ^.value := state.user),
          <.button("Create")
        )
      )
  }
}
