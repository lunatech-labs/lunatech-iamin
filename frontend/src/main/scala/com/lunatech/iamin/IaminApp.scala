package com.lunatech.iamin

import japgolly.scalajs.react.component.Scala
import japgolly.scalajs.react.component.ScalaFn.Component
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{CtorType, _}
import org.scalajs.dom.html.{Div, LI}

import scala.concurrent.Future

object IaminApp {

  case class AppState(items: List[String], user: String)

  val component: Scala.Component[Unit, AppState, AppBackend, CtorType.Nullary] = ScalaComponent
    .builder[Unit]("Iamin")
    .initialState(AppState(Nil, ""))
    .renderBackend[AppBackend]
    .build

  class AppBackend($: BackendScope[Unit, AppState]) {

    def onChange(e: ReactEventFromInput): Callback = {
      val newValue = e.target.value
      $.modState(_.copy(user = newValue))
    }

    def postUser(name: String): Future[User] = IaminAPI.postUser(name)

    def handleSubmit(event: ReactEventFromInput): Callback = {
      event.preventDefaultCB >> {
        $.modState(state => {
          postUser(state.user)
          AppState(state.items :+ state.user, "")
        })
      }
    }

    def createItem(itemText: String): VdomTagOf[LI] = <.li(itemText)

    def render(state: AppState): VdomTagOf[Div] =
      <.div(
        <.form(^.onSubmit ==> handleSubmit,
          <.input(^.onChange ==> onChange, ^.value := state.user),
          <.button("Create")
        ),
        UserList(state.items)
      )
  }

  val UserList: Component[List[String], CtorType.Props] = ScalaFnComponent[List[String]] { props =>
    def createItem(itemText: String) = <.li(itemText)

    <.ul(props.sorted map createItem: _*)
  }

}