package spatutorial.client.modules

import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import spatutorial.client.SPAMain.{DashboardLoc, Loc, MaintainUsersLoc }
import spatutorial.client.components.Bootstrap.CommonStyle
import spatutorial.client.components.Icon._
import spatutorial.client.components._
import spatutorial.client.services._
import scalacss.ScalaCssReact._

object MainMenu {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], currentLoc: Loc, proxy: ModelProxy[Option[Int]])

  private case class MenuItem(idx: Int, label: (Props) => VdomNode, icon: Icon, location: Loc)

  private def buildMaintainUsersMenu(props: Props): VdomElement = {
    val userCount = props.proxy().getOrElse(0)
    <.span(
      <.span("Users "),
      <.span(bss.labelOpt(CommonStyle.danger), bss.labelAsBadge, userCount).when(userCount > 0)
    )
  }

  private val menuItems = Seq(
    MenuItem(1, _ => "Dashboard", Icon.dashboard, DashboardLoc),
    MenuItem(2, buildMaintainUsersMenu, Icon.check, MaintainUsersLoc)
  )

  private class Backend($: BackendScope[Props, Unit]) {
    def mounted(props: Props) =
      Callback.when(props.proxy.value.isEmpty)(props.proxy.dispatchCB(RefreshUsers))

    def render(props: Props) = {
      <.ul(bss.navbar)(
        menuItems.toVdomArray(item =>
          <.li(^.key := item.idx, (^.className := "active").when(props.currentLoc == item.location),
          props.router.link(item.location)(item.icon, " ", item.label(props))
        ))
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("MainMenu")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(ctl: RouterCtl[Loc], currentLoc: Loc, proxy: ModelProxy[Option[Int]]): VdomElement =
    component(Props(ctl, currentLoc, proxy))
}
