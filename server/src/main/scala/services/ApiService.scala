package services

import java.util.{Date, UUID}

import repos.UserRepository
import spatutorial.shared._

import scala.concurrent.{Await, CanAwait}
import scala.concurrent.duration.Duration

class ApiService(repo: UserRepository) extends Api {

  override def welcomeMsg(name: String): String =
    s"Welcome to SPA, $name! Time is now ${new Date}"

  override def getAllUsers(): Seq[User] = {
    Await.result(repo.getAllUsers(), Duration.Inf)
  }

  override def updateUser(item: User): Seq[User] = {
    val users = Await.result(repo.getAllUsers(), Duration.Inf)
    if(users.exists(_.id == item.id)) {
      Await.result(repo.update(item.id, item.name), Duration.Inf)
      println(s"User item was updated: $item")
    } else {
      val newItem = Await.result(repo.create(item.name), Duration.Inf)
      println(s"User item was added: $newItem")
    }
    Await.result(repo.getAllUsers(), Duration.Inf)
  }

  override def deleteUser(id: Int): Seq[User] = {
    Await.result(repo.delete(id), Duration.Inf)
    Await.result(repo.getAllUsers(), Duration.Inf)
  }
}
