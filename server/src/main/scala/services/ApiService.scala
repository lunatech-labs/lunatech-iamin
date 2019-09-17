package services

import java.util.{UUID, Date}

import spatutorial.shared._

class ApiService extends Api {

  var users = Seq(UserItem("gjfdklg8gd", "Dracula"), UserItem("fjsd822", "Pietje"))

  override def welcomeMsg(name: String): String =
    s"Welcome to SPA, $name! Time is now ${new Date}"

  override def getAllUsers(): Seq[UserItem] = {
    users
  }

  override def updateUser(item: UserItem): Seq[UserItem] = {
    if(users.exists(_.id == item.id)) {
      users = users.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"User item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      users :+= newItem
      println(s"User item was added: $newItem")
    }
    Thread.sleep(300)
    users
  }
}
