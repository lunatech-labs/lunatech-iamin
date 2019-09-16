package com.lunatech.iamin.modules

import com.lunatech.iamin.components.Bootstrap.{Button, Modal, Panel}
import com.lunatech.iamin.components.{GlobalStyles, UserList}
import com.lunatech.iamin.services.{DeleteUser, RefreshUsers, UpdateUser, Users}
import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react.component.Scala.{Component, Unmounted}
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{CtorType, _}
import scalacss.ScalaCssReact._

case class UserItem(id: String, name: String)

object UserPanel {

  case class Props(proxy: ModelProxy[Pot[Users]])

  case class State(selectedItem: Option[UserItem] = None, showUserForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props): Callback =
      Callback.when(props.proxy().isEmpty)({
        props.proxy.dispatchCB(RefreshUsers)
      })

    def editUser(item: Option[UserItem]): CallbackTo[Unit] =
      $.modState(s => s.copy(selectedItem = item, showUserForm = true))

    def userEdited(item: UserItem, cancelled: Boolean): CallbackTo[Unit] = {
      val cb = if (cancelled) {
        Callback.log("User editing cancelled")
      } else {
        Callback.log(s"User edited: $item") >>
          $.props >>= (_.proxy.dispatchCB(UpdateUser(item)))
      }
      cb >> $.modState(s => s.copy(showUserForm = false))
    }

    def render(p: Props, s: State): Unmounted[Panel.Props, Unit, Unit] = {
      Panel(Panel.Props("A list of users"),
        <.div(
        p.proxy().renderFailed(_ => "Error loading"),
        p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy().render(users => {
          UserList(users.items, item => {
            p.proxy.dispatchCB(UpdateUser(item))
          }, item => {
              editUser(Some(item))
            }, item => {
              p.proxy.dispatchCB(DeleteUser(item))
            }
          )
        }
        ),
        Button(Button.Props(editUser(None)), " New")),
        if (s.showUserForm) {
          println("showing form")
          UserForm(UserForm.Props(s.selectedItem, userEdited))
        } else {
          println("not showing form")
          VdomArray.empty()
        })
    }
  }

  val component: Component[Props, State, Backend, CtorType.Props] = ScalaComponent.builder[Props]("USER")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[Users]]): Unmounted[Props, State, Backend] = component(Props(proxy))
}

object UserForm {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(item: Option[UserItem], submitHandler: (UserItem, Boolean) => Callback)

  case class State(item: UserItem, cancelled: Boolean = true)

  class Backend(t: BackendScope[Props, State]) {
    def submitForm(): Callback = {
      t.modState(s => s.copy(cancelled = false))
    }

    def formClosed(state: State, props: Props): Callback =
      props.submitHandler(state.item, state.cancelled)

    def updateName(e: ReactEventFromInput): CallbackTo[Unit] = {
      val text = e.target.value
      t.modState(s => s.copy(item = s.item.copy(name = text)))
    }

    def render(p: Props, s: State): Unmounted[Modal.Props, Unit, Modal.Backend] = {
      val headerText = if (s.item.id == "") "Add new user" else "Edit user"
      Modal(Modal.Props(
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide), <.h4(headerText)),
        footer = hide => <.span(Button(Button.Props(submitForm() >> hide), "OK")),
        closed = formClosed(s, p)),
        <.div(bss.formGroup,
          <.label(^.`for` := "name", "Name"),
          <.input.text(bss.formControl, ^.id := "name", ^.value := s.item.name,
            ^.placeholder := "write name", ^.onChange ==> updateName))
      )
    }
  }

  val component: Component[Props, State, Backend, CtorType.Props] = ScalaComponent.builder[Props]("UserForm")
    .initialStateFromProps(p => State(p.item.getOrElse(UserItem("some_user_id", "some_user_name"))))
    .renderBackend[Backend]
    .build

  def apply(props: Props): Unmounted[Props, State, Backend] = component(props)
}
