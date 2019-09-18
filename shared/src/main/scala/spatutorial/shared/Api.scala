package spatutorial.shared

trait Api {

  def getAllUsers(): Seq[User]

  def updateUser(item: User): Seq[User]

  // message of the day
  def welcomeMsg(name: String): String

  def deleteUser(item: Int): Seq[User]
}
