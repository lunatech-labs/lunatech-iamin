package com.lunatech.iamin.services

import autowire._
import com.lunatech.iamin.Api
import com.lunatech.iamin.modules.UserItem
import diode._
import diode.data.{Empty, Pot, Ready}
import diode.react.ReactConnector
import boopickle.Default._

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

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
      effectOnly(Effect(IaminAPI.fetchUsers().map(UpdateAllUsers)))
    case UpdateAllUsers(users) => {
      updated(Ready(Users(users)))
    }
    case UpdateUser(item) =>
      updated(value.map(_.updated(item)), Effect(AjaxPostClient[Api].updateUser(item).call().map(UpdateAllUsers)))
    case DeleteUser(item) => {
      updated(value.map(_.remove(item)),Effect(IaminAPI.deleteUser(item.id).map(_ => RefreshUsers)))
    }
  }
}

object AppCircuit extends Circuit[AppModel] with ReactConnector[AppModel] {
  def initialModel = AppModel(Empty)

  override val actionHandler: HandlerFunction = new UserHandler(zoomRW(_.users)((m, v) => m.copy(users = v)))
}
