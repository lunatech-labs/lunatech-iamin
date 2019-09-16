package com.lunatech.iamin

import com.lunatech.iamin.modules.UserItem

trait Api {
  def getAllUsers(): Seq[UserItem]
  def updateUser(item: UserItem): Seq[UserItem]
  def deleteUser(itemId: String): Seq[UserItem]
}
