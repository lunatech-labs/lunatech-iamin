package com.lunatech.iamin
import com.lunatech.iamin.CssSettings._
import com.lunatech.iamin.components.GlobalStyles
import com.lunatech.iamin.modules._
import com.lunatech.iamin.services.{AppCircuit, Reset}
import japgolly.scalajs.react.extra.router.{Resolution, RouterConfigDsl, RouterCtl, _}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom

import scala.scalajs.js.annotation.JSExport

object App {

  sealed trait Loc

  case object HomeLoc extends Loc

  case object DashboardLoc extends Loc

  case object TodoLoc extends Loc

  case object CounterDashboardLoc extends Loc

  val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
    import dsl._

    (staticRoute(root, HomeLoc) ~> renderR(ctl => AppCircuit.wrap(_.counter)(proxy => CounterDashboard(ctl, proxy)))
      ).notFound(redirectToPage(HomeLoc)(Redirect.Replace))
  }.renderWith(layout)

  //  val todoCountWrapper = SPACircuit.connect(_.todos.map(_.items.count(!_.completed)).toOption)
  def layout(c: RouterCtl[Loc], r: Resolution[Loc]) = {
    val counterWrapper = AppCircuit.connect(_.counter.toOption)
    <.div(
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "SPA Tutorial")),
          <.div(^.className := "collapse navbar-collapse"
//            , counterWrapper(proxy => MainMenu(c, r.page, proxy))
          )
        )
      ),
      <.div(^.className := "container", r.render())
    )
  }

  @JSExport
  def main(args: Array[String]): Unit = {

    GlobalStyles.addToDocument()
    val rootElement = dom.document.getElementById("mount")
    AppCircuit(Reset)
    val router = Router(BaseUrl.until_#, routerConfig)
    // tell React to render the router in the document body
    router().renderIntoDOM(rootElement)
  }
}