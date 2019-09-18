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

    def id = column[String]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UsersTable]

  def create(id: String, name: String): Future[User] = db.run {
    (users.map(p => (p.id, p.name))
      returning users.map(_.id)
      into ((name, id) => User(id, name._1))) += (id, name)
  }

  def getAllUsers(): Future[Seq[User]] = db.run {
    users.result
  }
}
