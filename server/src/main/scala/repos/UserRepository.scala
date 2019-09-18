package repos

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import spatutorial.shared.User
import slick.jdbc.PostgresProfile._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(
  implicit ec: ExecutionContext
) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UsersTable]

  def create(name: String): Future[Int] = db.run {
    users.map(u => (u.name)) += (name)
  }

  def update(id: Int, name: String): Future[Int] = db.run {
    val q = for { u <- users if u.id === id } yield u.name
    q.update(name)
  }

  def getAllUsers(): Future[Seq[User]] = db.run {
    users.result
  }
}
