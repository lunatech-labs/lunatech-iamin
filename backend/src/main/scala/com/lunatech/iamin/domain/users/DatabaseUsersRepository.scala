package com.lunatech.iamin.domain.users

import java.time.LocalDateTime

import cats.effect.IO
import cats.implicits._
import com.lunatech.iamin.database.Profile.api._
import com.lunatech.iamin.database.tables.{Tables, UsersRow}
import com.lunatech.iamin.utils.AbstractDatabaseRepository

class DatabaseUsersRepository(db: Database) extends AbstractDatabaseRepository(db) with UsersRepository[IO] {

  override def getUsers(idOffset: Long = 0, limit: Int = Int.MaxValue): IO[Seq[User]] =
    execute {
      Tables.Users
        .filter(_.id > idOffset.bind)
        .take(limit)
        .result
    } map rowsToUsers

  override def getUser(id: Long): IO[Either[UserNotFound.type, User]] =
    execute {
      Tables.Users
        .filter(_.id === id.bind)
        .result
        .headOption
    } map { _.map(rowToUser).toRight(UserNotFound) }

  override def createUser(displayName: String): IO[User] =
    execute {
      Tables.Users
        .returning(Tables.Users.map(_.id))
        .into((row, newId) => row.copy(id = newId)) += UsersRow(0, displayName, LocalDateTime.now)
    } map rowToUser

  override def updateUser(user: User): IO[Either[UserNotFound.type, User]] =
    execute {
      Tables.Users
        .filter(_.id === user.id.bind)
        .map(_.displayName)
        .update(user.displayName)
    } map { affectedRows =>
      Either.cond(affectedRows eqv 1, user, UserNotFound)
    }

  override def deleteUser(id: Long): IO[Either[UserNotFound.type, Unit]] =
    execute {
      Tables.Users
        .filter(_.id === id.bind)
        .delete
    } map { affectedRows =>
      Either.cond(affectedRows eqv 1, (), UserNotFound)
    }

  private def rowToUser(row: UsersRow): User = User(row.id, row.displayName)

  private def rowsToUsers(rows: Seq[UsersRow]): Seq[User] = rows map rowToUser
}
