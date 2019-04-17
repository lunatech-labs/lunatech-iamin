package com.lunatech.iamin.repositories.impl

import java.time.LocalDateTime

import cats.effect.IO
import cats.implicits._
import com.lunatech.iamin.database.Profile.api._
import com.lunatech.iamin.database.tables.{Tables, UsersRow}
import com.lunatech.iamin.model
import com.lunatech.iamin.repositories.{AbstractDbRepository, UsersRepository}

class DbUsersRepository(db: Database) extends AbstractDbRepository(db) with UsersRepository[IO] {

  override def getUsers(idOffset: Long = 0, limit: Int = Int.MaxValue): IO[Seq[model.User]] =
    execute {
      Tables.Users
        .filter(_.id > idOffset.bind)
        .take(limit)
        .result
    } map rowsToUsers

  override def getUser(id: Long): IO[Either[model.UserNotFound.type, model.User]] =
    execute {
      Tables.Users
        .filter(_.id === id.bind)
        .result
        .headOption
    } map { _.map(rowToUser).toRight(model.UserNotFound) }

  override def createUser(displayName: String): IO[model.User] =
    execute {
      Tables.Users
        .returning(Tables.Users.map(_.id))
        .into((row, newId) => row.copy(id = newId)) += UsersRow(0, displayName, LocalDateTime.now)
    } map rowToUser

  override def updateUser(user: model.User): IO[Either[model.UserNotFound.type, model.User]] =
    execute {
      Tables.Users
        .filter(_.id === user.id.bind)
        .update(userToRow(user))
    } map { affectedRows =>
      Either.cond(affectedRows eqv 1, user, model.UserNotFound)
    }

  override def deleteUser(id: Long): IO[Either[model.UserNotFound.type, Unit]] =
    execute {
      Tables.Users
        .filter(_.id === id.bind)
        .delete
    } map { affectedRows =>
      Either.cond(affectedRows eqv 1, (), model.UserNotFound)
    }

  private def userToRow(user: model.User): UsersRow = UsersRow(user.id, user.displayName, user.created)

  private def rowToUser(row: UsersRow): model.User = model.User(row.id, row.displayName, row.created)

  private def rowsToUsers(rows: Seq[UsersRow]): Seq[model.User] = rows map rowToUser
}
