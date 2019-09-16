package com.lunatech.iamin

import com.lunatech.iamin.CssSettings._
import com.lunatech.iamin.components.GlobalStyles
import com.lunatech.iamin.modules._
import com.lunatech.iamin.services.AppCircuit
import japgolly.scalajs.react.extra.router.{Resolution, RouterConfigDsl, RouterCtl, _}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import org.scalajs.dom.html.Div

import scala.scalajs.js.annotation.JSExport

object App {

  sealed trait Loc

  case object DashboardLocl extends Loc

  case object UserLoc extends Loc

  val routerConfig: RouterConfig[Loc] = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._
    val userPanelWrapper = AppCircuit.connect(_.users)

    (staticRoute(root, UserLoc) ~> renderR(ctl => userPanelWrapper(UserPanel(_)))
      ).notFound(redirectToPage(DashboardLocl)(Redirect.Replace))
  }.renderWith(layout)

  def layout(c: RouterCtl[Loc], r: Resolution[Loc]): VdomTagOf[Div] = {
    <.div(
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "Lunatech Iamin")),
          <.div(^.className := "collapse navbar-collapse"
          )
        )
      ),
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    GlobalStyles.addToDocument()
    val router = Router(BaseUrl.until_#, routerConfig)
    router().renderIntoDOM(dom.document.getElementById("root"))
  }
}