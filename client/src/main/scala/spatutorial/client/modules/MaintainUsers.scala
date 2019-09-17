package spatutorial.client.modules

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components._
import spatutorial.client.logger._
import spatutorial.client.services._
import spatutorial.shared._

import scalacss.ScalaCssReact._

object MaintainUsers {

  case class Props(proxy: ModelProxy[Pot[Users]])

  case class State(selectedItem: Option[UserItem] = None, showUserForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
    // dispatch a message to refresh the users, which will cause UserStore to fetch users from the server
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(RefreshUsers))

    def editUser(item: Option[UserItem]) =
    // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showUserForm = true))

    def userEdited(item: UserItem, cancelled: Boolean) = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("User editing cancelled")
      } else {
        Callback.log(s"User edited: $item") >>
          $.props >>= (_.proxy.dispatchCB(UpdateUser(item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showUserForm = false))
    }

    def render(p: Props, s: State) =
      Panel(Panel.Props("What needs to be done"), <.div(
        p.proxy().renderFailed(ex => "Error loading"),
        p.proxy().renderPending(_ > 500, _ => "Loading..."),
        p.proxy().render(users => UserList(users.items, item => p.proxy.dispatchCB(UpdateUser(item)),
          item => editUser(Some(item)), item => p.proxy.dispatchCB(DeleteUser(item)))),
        Button(Button.Props(editUser(None)), Icon.plusSquare, " New")),
        // if the dialog is open, add it to the panel
        if (s.showUserForm) UserForm(UserForm.Props(s.selectedItem, userEdited))
        else // otherwise add an empty placeholder
          VdomArray.empty())
  }

  // create the React component for To Do management
  val component = ScalaComponent.builder[Props]("USER")
    .initialState(State()) // initial state from UserStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[Pot[Users]]) = component(Props(proxy))
}

object UserForm {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(item: Option[UserItem], submitHandler: (UserItem, Boolean) => Callback)

  case class State(item: UserItem, cancelled: Boolean = true)

  class Backend(t: BackendScope[Props, State]) {
    def submitForm(): Callback = {
      // mark it as NOT cancelled (which is the default)
      t.modState(s => s.copy(cancelled = false))
    }

    def formClosed(state: State, props: Props): Callback =
    // call parent handler with the new item and whether form was OK or cancelled
      props.submitHandler(state.item, state.cancelled)

    def updateName(e: ReactEventFromInput) = {
      val text = e.target.value
      // update UserItem content
      t.modState(s => s.copy(item = s.item.copy(name = text)))
    }

    def render(p: Props, s: State) = {
      log.debug(s"User is ${if (s.item.id == "") "adding" else "editing"} a user or two")
      val headerText = if (s.item.id == "") "Add new user" else "Edit user"
      Modal(Modal.Props(
        // header contains a cancel button (X)
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.h4(headerText)),
        // footer has the OK button that submits the form before hiding it
        footer = hide => <.span(Button(Button.Props(submitForm() >> hide), "OK")),
        // this is called after the modal has been hidden (animation is completed)
        closed = formClosed(s, p)),
        <.div(bss.formGroup,
          <.label(^.`for` := "name", "Name"),
          <.input.text(bss.formControl, ^.id := "name", ^.value := s.item.name,
            ^.placeholder := "write name", ^.onChange ==> updateName))
      )
    }
  }

  val component = ScalaComponent.builder[Props]("UserForm")
    .initialStateFromProps(p => State(p.item.getOrElse(UserItem("foo", "bar"))))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}