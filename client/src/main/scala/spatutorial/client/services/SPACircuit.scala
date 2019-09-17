package spatutorial.client.services

import autowire._
import diode._
import diode.data._
import diode.util._
import diode.react.ReactConnector
import spatutorial.shared.{Api, UserItem}
import boopickle.Default._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

case class UpdateMotd(potResult: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
  override def next(value: Pot[String]) = UpdateMotd(value)
}

case class RootModel(motd: Pot[String], users: Pot[Users])

class MotdHandler[M](modelRW: ModelRW[M, Pot[String]]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action: UpdateMotd =>
      val updateF = action.effect(AjaxClient[Api].welcomeMsg("User X").call())(identity _)
      action.handleWith(this, updateF)(PotAction.handler())
  }
}


case object RefreshUsers extends Action

case class UpdateAllUsers(users: Seq[UserItem]) extends Action

case class UpdateUser(item: UserItem) extends Action

case class DeleteUser(item: UserItem) extends Action

case class AppModel(users: Pot[Users])

case class Users(items: Seq[UserItem]) {
  def updated(newItem: UserItem): Users = {
    items.indexWhere(_.id == newItem.id) match {
      case -1 =>
        Users(items :+ newItem)
      case idx =>
        Users(items.updated(idx, newItem))
    }
  }

  def remove(item: UserItem) = Users(items.filterNot(_ == item))
}

class UserHandler[M](modelRW: ModelRW[M, Pot[Users]]) extends ActionHandler(modelRW) {
  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case RefreshUsers =>
      effectOnly(Effect(AjaxClient[Api].getAllUsers().call().map(UpdateAllUsers)))
    case UpdateAllUsers(users) =>
      updated(Ready(Users(users)))
    case UpdateUser(item) =>
      updated(Ready(Users(Seq())))
    case DeleteUser(item) =>
      updated(Ready(Users(Seq())))
  }
}

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(Empty, Empty)
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new MotdHandler(zoomRW(_.motd)((m, v) => m.copy(motd = v))),
    new UserHandler(zoomRW(_.users)((m, v) => m.copy(users = v)))

  )
}