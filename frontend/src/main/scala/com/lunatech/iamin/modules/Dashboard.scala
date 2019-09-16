package com.lunatech.iamin.modules

import com.lunatech.iamin.App.Loc
import com.lunatech.iamin.components.Bootstrap.Panel
import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react.component.Scala.{Component, Unmounted}
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{CtorType, _}

import scala.language.existentials

object Dashboard {

  case class Props(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Int]])

  case class State(counterWrapper: ReactConnectProxy[Pot[Int]])

  private val counterDashboardComponent = ScalaComponent.builder[Props]("Counter")
    .initialStateFromProps(props => State(props.proxy.connect(m => m)))
    .renderPS { (_, props, state) =>
      <.div(
        <.h2("Counter"),
        state.counterWrapper(CounterComponent(_)),
      )
    }
    .build

  def apply(router: RouterCtl[Loc], proxy: ModelProxy[Pot[Int]]): Unmounted[Props, State, Unit] = counterDashboardComponent(Props(router, proxy))
}

object CounterComponent {

  val counterComponent: Component[ModelProxy[Pot[Int]], Unit, Unit, CtorType.Props] = ScalaComponent.builder[ModelProxy[Pot[Int]]]("counter")
    .render_P { proxy =>
      Panel(Panel.Props("Click the button"),
        proxy().renderPending(_ > 500, _ => <.p("Loading...")),
        proxy().renderFailed(ex => <.p("Failed to load")),
        proxy().render(m => <.p(m)))
    }
    .build

  def apply(proxy: ModelProxy[Pot[Int]]): Unmounted[ModelProxy[Pot[Int]], Unit, Unit] = counterComponent(proxy)
}
