package com.lunatech.iamin.services

import java.util.UUID

import com.lunatech.iamin.Api
import com.lunatech.iamin.modules.UserItem

class ApiService extends Api {
  var users: Seq[UserItem] = Seq(
    UserItem("h43fhjkds", "Pietje Puk"),
    UserItem("l33tm0f0", "Dwayne Johnsonz"),
  )

  override def getAllUsers(): Seq[UserItem] = {
    println(s"Sending ${users.size} User items")
    users
  }

  override def updateUser(item: UserItem): Seq[UserItem] = {
    if (users.exists(_.id == item.id)) {
      users = users.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"User item was updated: $item")
    } else {
      val newItem = item.copy(id = UUID.randomUUID().toString)
      users :+= newItem
      println(s"User item was added: $newItem")
    }
    Thread.sleep(300)
    users
  }

  override def deleteUser(itemId: String): Seq[UserItem] = {
    println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    users = users.filterNot(_.id == itemId)
    users
  }
}
