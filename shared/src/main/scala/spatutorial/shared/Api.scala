package spatutorial.shared

trait Api {

  def getAllUsers(): Seq[UserItem]

  def updateUser(item: UserItem): Seq[UserItem]
  // message of the day
  def welcomeMsg(name: String): String

}
