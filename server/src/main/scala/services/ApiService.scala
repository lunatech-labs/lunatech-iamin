package services

import java.util.{UUID, Date}

import spatutorial.shared._

class ApiService extends Api {

  override def welcomeMsg(name: String): String =
    s"Welcome to SPA, $name! Time is now ${new Date}"

  override def getAllUsers(): Seq[UserItem] = {
    Seq(UserItem("gjfdklg8gd", "Dracula"), UserItem("fjsd822", "Pietje"))
  }
}
