package com.lunatech.iamin.core.user

import com.lunatech.iamin.{User, UserId}
import scalaz.zio.ZIO

package object repository extends UserRepository.Service[UserRepository] {

  def createUser(create: User.Create): ZIO[UserRepository, Nothing, User] =
    ZIO.accessM(_.userRepository.createUser(create))

  def getUser(id: UserId): ZIO[UserRepository, Nothing, Option[User]] =
    ZIO.accessM(_.userRepository.getUser(id))

  def listUsers(offset: UserId, limit: Int): ZIO[UserRepository, Nothing, List[User]] =
    ZIO.accessM(_.userRepository.listUsers(offset, limit))

  def updateUser(id: UserId, update: User.Update): ZIO[UserRepository, Nothing, Option[User]] =
    ZIO.accessM(_.userRepository.updateUser(id, update))

  def deleteUser(id: UserId): ZIO[UserRepository, Nothing, Boolean] =
    ZIO.accessM(_.userRepository.deleteUser(id))
}