package com.lunatech.iamin.domain.users

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import cats.effect.IO

import scala.collection.JavaConverters._

class InMemoryUsersRepository extends UsersRepository[IO] {

  private[this] val users = new ConcurrentHashMap[Long, User]().asScala
  private[this] val ids = new AtomicLong(0L)

  override def getUsers(idOffset: Long, limit: Int): IO[Seq[User]] =
    IO.pure(users.values.toSeq)

  override def getUser(id: Long): IO[Either[UserNotFound.type, User]] =
    IO.pure(users.get(id).toRight(UserNotFound))

  override def createUser(displayName: String): IO[User] =
    for {
      id   <- IO(ids.incrementAndGet())
      user <- IO.pure(User(id, displayName))
      _    <- IO(users.put(id, user))
    } yield user

  override def updateUser(user: User): IO[Either[UserNotFound.type, User]] =
    getUser(user.id)
      .map {
        case notFound@Left(_) => notFound
        case Right(existing) => {
          val updated = existing.copy(displayName = user.displayName)
          users.update(user.id, updated)
          Right(updated)
        }
      }

  override def deleteUser(id: Long): IO[Either[UserNotFound.type, Unit]] =
    IO(users.remove(id).toRight(UserNotFound).map(_ => ()))
}