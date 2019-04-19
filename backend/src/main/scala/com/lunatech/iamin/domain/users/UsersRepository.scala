package com.lunatech.iamin.domain.users

trait UsersRepository[F[_]] {

  def getUsers(idOffset: Long = 0, limit: Int = Int.MaxValue): F[Seq[User]]

  def getUser(id: Long): F[Either[UserNotFound.type, User]]

  def createUser(displayName: String): F[User]

  def updateUser(user: User): F[Either[UserNotFound.type, User]]

  def deleteUser(id: Long): F[Either[UserNotFound.type, Unit]]
}
