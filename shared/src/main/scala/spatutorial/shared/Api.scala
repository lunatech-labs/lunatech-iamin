package spatutorial.shared

trait Api {

  def getAllUsers(): Seq[UserItem]

  // message of the day
  def welcomeMsg(name: String): String

}
