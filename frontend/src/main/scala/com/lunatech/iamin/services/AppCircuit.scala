package com.lunatech.iamin.services

import diode._
import diode.data.{Empty, Pot}
import diode.react.ReactConnector

case class AppModel(counter: Pot[Int])

case class Increase(amount: Int) extends Action

case class Decrease(amount: Int) extends Action

case object Reset extends Action

object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  def initialModel = AppModel(Empty)
  override val actionHandler: HandlerFunction = new CounterActionHandler(zoomRW(_.counter)((m, v) => m.copy(counter = v)))
}

class CounterActionHandler[AppModel](modelRW: ModelRW[AppModel, Pot[Int]]) extends ActionHandler(modelRW) {
  override protected def handle: PartialFunction[Any, ActionResult[AppModel]] = {
    case Increase(a) => updated(Pot.fromOption(Option(202)))
    case Decrease(a) => updated(Pot.fromOption(Option(0)))
    case Reset => updated(Pot.fromOption(Option(101)))
  }
}