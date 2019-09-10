package com.lunatech.iamin

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object IaminApp {

  case class AppState(items: List[String], user: String)

  val component = ScalaComponent
    .builder[Unit]("Iamin")
    .initialState(AppState(Nil, ""))
    .renderBackend[AppBackend]
    .build

  class AppBackend($: BackendScope[Unit, AppState]) {

    def onChange(e: ReactEventFromInput) = {
      val newValue = e.target.value
      println(newValue)
      $.modState(_.copy(user = newValue))
    }

    def handleSubmit(e: ReactEventFromInput) = {
      e.preventDefaultCB >>
      $.modState(s => AppState(s.items :+ s.user, ""))
    }

    def createItem(itemText: String) = <.li(itemText)

    def render(state: AppState) =
      <.div(
        <.form(^.onSubmit ==> handleSubmit,
          <.input(^.onChange ==> onChange, ^.value := state.user),
          <.button("Create")
        ),
        UserList(state.items)
      )
  }

  val UserList = ScalaFnComponent[List[String]]{ props =>
    def createItem(itemText: String) = <.li(itemText)
    println(props.sorted)
    <.ul(props.sorted map createItem: _*)
  }

}
