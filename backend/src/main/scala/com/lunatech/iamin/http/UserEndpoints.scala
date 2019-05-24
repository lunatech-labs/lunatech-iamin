package com.lunatech.iamin.http

import com.lunatech.iamin.core.idcodec.service._
import com.lunatech.iamin.core.user.repository._
import com.lunatech.iamin.endpoints.definitions._
import com.lunatech.iamin.endpoints.users._
import com.lunatech.iamin.{User, UserId}
import org.http4s.HttpRoutes
import scalaz.zio.interop.catz._
import scalaz.zio.{TaskR, UIO, ZIO}

final class UserEndpoints[R <: UserRepository with IdCodec] {
  import UserEndpoints._

  type UserEndpointETask[A] = TaskR[R, A]

  def routes: HttpRoutes[UserEndpointETask] = new UsersResource[UserEndpointETask].routes(new UsersHandlerImpl)

  private class UsersHandlerImpl extends UsersHandler[UserEndpointETask] {

    override def postUsers(respond: PostUsersResponse.type)(body: PostUserRequestJson): UserEndpointETask[PostUsersResponse] = {
      for {
        createdUser <- createUser(User.Create(body.name))
        userId      <- encodeId(createdUser.id.value)
      } yield respond.Ok(UserResponseJson(userId, createdUser.name))
    }

    override def getUsers(respond: GetUsersResponse.type)(maybeHashedOffset: Option[String], maybeLimit: Option[Int]): UserEndpointETask[GetUsersResponse] = {
      for {
        offset     <- decodeId(maybeHashedOffset.getOrElse("")).fold(_ => UserId.Zero, maybeOffset => maybeOffset.fold(UserId.Zero)(UserId(_)))
        limit       = maybeLimit.getOrElse(DefaultLimit) min MaxLimit
        users      <- listUsers(offset, limit)
        ids0       <- UIO(users.map(_.id)).map(_.map(id => encodeId(id.value)))
        ids        <- ZIO.collectAll(ids0)
        jsonUsers   = (users zip ids).map { case (user, hashedId) => UserResponseJson(hashedId, user.name)}
        nextOffset  = if (jsonUsers.length == limit) jsonUsers.lastOption.map(_.id) else Option.empty[String]
      } yield respond.Ok(UsersResponseJson(jsonUsers.toIndexedSeq, nextOffset = nextOffset))
    }

    override def getUsersById(respond: GetUsersByIdResponse.type)(hashedUserId: String): UserEndpointETask[GetUsersByIdResponse] = {
      (for {
        userId    <- decodeId(hashedUserId).fold(_ => UserId.Zero, _.fold(UserId.Zero)(UserId(_)))
        maybeUser <- getUser(userId)
      } yield maybeUser) map {
        case None       => respond.NotFound(notFoundProblem(s"user $hashedUserId was not found"))
        case Some(user) => respond.Ok(UserResponseJson(hashedUserId, user.name))
      }
    }

    override def patchUserById(respond: PatchUserByIdResponse.type)(hashedUserId: String, body: PatchUserRequestJson): UserEndpointETask[PatchUserByIdResponse] = {
      (for {
        userId           <- decodeId(hashedUserId).fold(_ => UserId.Zero, _.fold(UserId.Zero)(UserId(_)))
        maybeUpdatedUser <- updateUser(userId, User.Update(body.name))
      } yield maybeUpdatedUser)
        .map {
          case None       => respond.NotFound(notFoundProblem(s"user $hashedUserId was not found"))
          case Some(user) => respond.Ok(UserResponseJson(hashedUserId, user.name))
        }
    }

    override def deleteUserById(respond: DeleteUserByIdResponse.type)(hashedUserId: String): UserEndpointETask[DeleteUserByIdResponse] = {
      (for {
        userId  <- decodeId(hashedUserId).fold(_ => UserId.Zero, _.fold(UserId.Zero)(UserId(_)))
        success <- deleteUser(userId)
      } yield success) map {
        case false => respond.NotFound(notFoundProblem(s"user $hashedUserId was not found"))
        case true  => respond.NoContent
      }
    }
  }
}

object UserEndpoints {
  val DefaultLimit = 10
  val MaxLimit = 100
}