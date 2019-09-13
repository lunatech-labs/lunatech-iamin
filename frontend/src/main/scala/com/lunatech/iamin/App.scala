package com.lunatech.iamin

import com.lunatech.iamin.CssSettings._
import com.lunatech.iamin.components.{CounterView, GlobalStyles}
import com.lunatech.iamin.modules._
import com.lunatech.iamin.services.{AppCircuit, Reset, SPACircuit}
import japgolly.scalajs.react.extra.router.{Resolution, RouterConfigDsl, RouterCtl, _}
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom
import org.scalajs.dom.Element
import org.scalajs.dom.html.Div
import scalatags.JsDom.all._

import scala.scalajs.js.annotation.JSExport

object App {

  val counter = new CounterView(AppCircuit.zoom(_.counter), AppCircuit)

  sealed trait Loc

  case object DashboardLoc extends Loc

  case object TodoLoc extends Loc

  @JSExport
  def main(args: Array[String]): Unit = {
    GlobalStyles.addToDocument()
    val rootElement = dom.document.getElementById("mount")
    AppCircuit.subscribe(AppCircuit.zoom(identity))(_ => render(rootElement))
    AppCircuit(Reset)
//    val routerConfig = RouterConfigDsl[Loc].buildConfig { dsl =>
//      import dsl._
//
//      val todoWrapper = SPACircuit.connect(_.todos)
//      (
//        staticRoute(root, DashboardLoc) ~> renderR(ctl => SPACircuit.wrap(_.motd)(proxy => Dashboard(ctl, proxy)))
//          |
//          staticRoute("#todo", TodoLoc) ~> renderR(ctl => todoWrapper(Todo(_)))
//        ).notFound(redirectToPage(DashboardLoc)(Redirect.Replace))
//    }
//      .renderWith(layout)
//
//    val router = Router(BaseUrl.until_#, routerConfig)
//    // tell React to render the router in the document body
//    router().renderIntoDOM(rootElement)
  }

  def render(root: Element) = {
    val e = div(
      cls := "container",
      h1("Simple counter example"),
      p(a(href := "https://github.com/suzaku-io/diode/tree/master/examples/simple", "Source code")),
      counter.render // renders the counter view
    ).render
    // clear and update contents
    root.innerHTML = ""
    root.appendChild(e)
  }

  def layout(c: RouterCtl[Loc], r: Resolution[Loc]): VdomTagOf[Div] = {
//    val todoCountWrapper = SPACircuit.connect(_.todos.map(_.items.count(!_.completed)).toOption)

    <.div(
      <.nav(^.className := "navbar navbar-inverse navbar-fixed-top",
        <.div(^.className := "container",
          <.div(^.className := "navbar-header", <.span(^.className := "navbar-brand", "SPA Tutorial")),
          <.div(^.className := "collapse navbar-collapse",
//            todoCountWrapper(proxy => MainMenu(c, r.page, proxy))
          )
        )
      ),
      <.div(^.className := "container", r.render())
    )
  }
}