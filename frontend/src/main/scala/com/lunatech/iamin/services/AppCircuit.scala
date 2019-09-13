package com.lunatech.iamin.services


import diode._

case class AppModel(counter: Int)

case class Increase(amount: Int) extends Action

case class Decrease(amount: Int) extends Action

case object Reset extends Action

object AppCircuit extends Circuit[AppModel] {
  // define initial value for the application model
  def initialModel = AppModel(0)

  // zoom into the model, providing access only to the
  val counterHandler = new ActionHandler(zoomTo(_.counter)) {
    override def handle = {
      case Increase(a) => updated(value + a)
      case Decrease(a) => updated(value - a)
      case Reset => updated(0)
    }
  }

  override val actionHandler: HandlerFunction = counterHandler
}
