package com.lunatech.iamin.core.user.repository

import com.lunatech.iamin.{User, UserId}
import scalaz.zio.ZIO

trait UserRepository {

  def userRepository: UserRepository.Service[Any]
}

object UserRepository {

  trait Service[R] {

    def createUser(create: User.Create): ZIO[R, Nothing, User]

    def getUser(id: UserId): ZIO[R, Nothing, Option[User]]

    def listUsers(offset: UserId, limit: Int = 10): ZIO[R, Nothing, List[User]]

    def updateUser(id: UserId, update: User.Update): ZIO[R, Nothing, Option[User]]

    def deleteUser(id: UserId): ZIO[R, Nothing, Boolean]
  }
}
