package com.lunatech.iamin.services

import com.lunatech.iamin.modules.UserItem
import japgolly.scalajs.react.raw.Empty
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.util.Failure

object IaminAPI {

  def fetchUsers(): Future[Seq[UserItem]] = {
    Ajax.get("http://localhost:8080/users") map { xhr =>
      val userListing = JSON.parse(xhr.responseText).asInstanceOf[ItemListing[User]]
      userListing.items.map(u => UserItem(u.id, u.name))
    }
  }

  def deleteUser(id: String): Future[Unit] = {
    Ajax.delete(s"http://localhost:8080/users/$id") map { _ => Unit }
  }
}

@js.native
trait User extends js.Object {
  def id: String

  def name: String
}

@js.native
trait ItemListing[T] extends js.Object {
  def items: js.Array[T]
}