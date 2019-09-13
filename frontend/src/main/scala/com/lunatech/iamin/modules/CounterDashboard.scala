package com.lunatech.iamin.modules

import com.lunatech.iamin.App.{Loc}
import com.lunatech.iamin.components.Bootstrap.{Button, Panel}
import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

import scala.language.existentials
import scala.util.Random

object CounterDashboard {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Int]])

  case class State(counterWrapper: ReactConnectProxy[Pot[Int]])

  private val counterDashboardComponent = ScalaComponent.builder[Props]("CounterDashboard")
    .initialStateFromProps(props => State(props.proxy.connect(m => m)))
    .renderPS { (_, props, state) =>
      <.div(
        // header, MessageOfTheDay and chart components
        <.h2("CounterDashboard"),
        state.counterWrapper(CounterComponent(_))
//        , <.div(props.router.link(TodoLoc)("Check your todos!"))
      )
    }
    .build

  def apply(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Int]]) = counterDashboardComponent(Props(router, proxy))
}

object CounterComponent {

  // create the React component for holding the Message of the Day
  val counterComponent = ScalaComponent.builder[ModelProxy[Pot[Int]]]("counter")
    .render_P { proxy =>
      Panel(Panel.Props("Message of the day"),
        proxy().renderPending(_ > 500, _ => <.p("Loading...")),
        proxy().renderFailed(ex => <.p("Failed to load")),
        proxy().render(m => <.p(m)))
    }
//    .componentDidMount(scope =>
//      // update only if Motd is empty
//      Callback.when(scope.props.value.isEmpty)(scope.props.dispatchCB(UpdateMotd()))
//    )
    .build

  def apply(proxy: ModelProxy[Pot[Int]]) = counterComponent(proxy)
}
