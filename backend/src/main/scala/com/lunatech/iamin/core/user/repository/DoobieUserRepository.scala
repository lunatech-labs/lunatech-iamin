package com.lunatech.iamin.core.user.repository

import cats.implicits._
import com.lunatech.iamin.{User, UserId}
import doobie._
import doobie.free.connection
import doobie.implicits._
import scalaz.zio.interop.catz._
import scalaz.zio.{Task, ZIO}

trait DoobieUserRepository extends UserRepository {
  import DoobieUserRepository._

  protected def xa: Transactor[Task]

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def userRepository: UserRepository.Service[Any] = new UserRepository.Service[Any] {

    def createUser(create: User.Create): ZIO[Any, Nothing, User] =
      SQL
        .insert(create)
        .withUniqueGeneratedKeys[Long]("id")
        .map(id => User(UserId(id), create.name))
        .transact(xa)
        .orDie

    def getUser(id: UserId): ZIO[Any, Nothing, Option[User]] =
      SQL
        .select(id)
        .option
        .transact(xa)
        .orDie

    def listUsers(offset: UserId, limit: Int): ZIO[Any, Nothing, List[User]] =
      SQL
        .selectMany(offset, limit)
        .to[List]
        .transact(xa)
        .orDie

    def updateUser(id: UserId, update: User.Update): ZIO[Any, Nothing, Option[User]] =
      (for {
        prevUser     <- SQL.select(id).option
        maybeUpdated  = prevUser.map(user => user.copy(name = update.name))
        _            <- maybeUpdated.fold(connection.unit)(_ => SQL.update(id, update).run.void)
      } yield maybeUpdated)
        .transact(xa)
        .orDie

    def deleteUser(id: UserId): ZIO[Any, Nothing, Boolean] =
      SQL
        .delete(id)
        .run
        .transact(xa)
        .map(_ > 0)
        .orDie
  }
}

object DoobieUserRepository {

  object SQL {

    def insert(create: User.Create): Update0 =
      sql"""
        INSERT INTO users (name)
        VALUES (${create.name})
      """.update

    def select(id: UserId): Query0[User] =
      sql"""
        SELECT id, name
        FROM users
        WHERE id = ${id.value}
      """.query[User]

    def selectMany(offset: UserId, limit: Int): Query0[User] =
      sql"""
        SELECT id, name
        FROM users
        WHERE id > ${offset.value}
        ORDER BY id ASC
        LIMIT ${limit.toLong}
      """.query[User]

    def update(id: UserId, update: User.Update): Update0 =
      sql"""
        UPDATE users SET name = ${update.name}
        WHERE id = ${id.value}
      """.update

    def delete(id: UserId): Update0 =
      sql"""
        DELETE FROM users
        WHERE id = ${id.value}
      """.update
  }
}
