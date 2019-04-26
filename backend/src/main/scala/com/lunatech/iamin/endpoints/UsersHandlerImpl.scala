package com.lunatech.iamin.endpoints

import cats.effect.Async
import cats.implicits._
import com.lunatech.iamin.domain.users.{User, UserService}
import com.lunatech.iamin.endpoints.definitions.{PatchUserRequestJson, PostUserRequestJson, UserResponseJson, UsersResponseJson}
import com.lunatech.iamin.endpoints.users._
import io.scalaland.chimney.dsl._

class UsersHandlerImpl[F[_] : Async](service: UserService[F]) extends UsersHandler[F] {

  override def getUsers(respond: GetUsersResponse.type)(): F[GetUsersResponse] = {
    service.list(0, 100) map { users =>
      UsersResponseJson(users.map(_.transformInto[UserResponseJson]).toIndexedSeq)
    } map {
      respond.Ok
    }
  }

  override def getUsersById(respond: GetUsersByIdResponse.type)(userId: Long): F[GetUsersByIdResponse] = {
    service.get(userId).fold[GetUsersByIdResponse](respond.NotFound)(respond.Ok(_))
  }

  override def postUsers(respond: PostUsersResponse.type)(body: PostUserRequestJson): F[PostUsersResponse] = {
    service.create(body.displayName).map(respond.Ok(_))
  }

  override def patchUserById(respond: PatchUserByIdResponse.type)(userId: Long, body: PatchUserRequestJson): F[PatchUserByIdResponse] = {
    service.update(userId, body.displayName).fold[PatchUserByIdResponse](respond.NotFound)(respond.Ok(_))
  }

  override def deleteUserById(respond: DeleteUserByIdResponse.type)(userId: Long): F[DeleteUserByIdResponse] = {
    service.delete(userId).fold[DeleteUserByIdResponse](respond.NotFound)(_ => respond.NoContent)
  }

  private implicit def userIntoUserResponseJson(user: User): UserResponseJson = user.transformInto[UserResponseJson]
}
