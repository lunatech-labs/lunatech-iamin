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
      $.modState(_.copy(user = newValue))
    }

    def postUser(name: String) = Callback.future {
      IaminAPI.postUser(name) map { user => $.setState(AppState(user.name))}
    }

    def render(state: AppState) =
      <.div(
        <.form(^.onSubmit --> postUser(state.user),
          <.input(^.onChange ==> onChange, ^.value := state.user),
          <.button("Create")
        )
      )


    // private val userInputState = $.zoomState(_.user)(value => _.copy(user = value))

    // def updateUser(event: ReactEvent): Callback = {
    //   userInputState.setState(event.target.nodeValue)
    // }

    // def getUser() = Callback.future {
    //   val userId = "BVmN9k2xkg3R0XOL"
    //   IaminAPI.getUser(userId).map {
    //     user => $.setState(AppState(user.name))
    //   }
    // }

    // def render(s: AppState) = {
    //   <.div(
    //     <.div(
    //       <.input(^.`type` := "text", ^.cls := "form-control",
    //             ^.value := "", ^.onChange ==> updateUser
    //           )
    //         ),
    //         <.div(^.cls := "col-xs-2",
    //           <.button(^.`type` := "button", ^.cls := "btn btn-primary custom-button-width",
    //             ^.onClick --> postUser(s.user),
    //             "Create"
    //           )
    //         ),
    //     <.p(s.user))
    // }

  }
}
