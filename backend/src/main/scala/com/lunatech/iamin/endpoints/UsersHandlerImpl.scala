package com.lunatech.iamin.endpoints

import cats.effect.Async
import cats.implicits._
import com.lunatech.iamin.domain.users.{User, UserRepository}
import com.lunatech.iamin.endpoints.definitions.{PatchUserRequestJson, PostUserRequestJson, UserResponseJson, UsersResponseJson}
import com.lunatech.iamin.endpoints.users._

class UsersHandlerImpl[F[_] : Async](userRepo: UserRepository[F]) extends UsersHandler[F] {

  override def getUsers(respond: GetUsersResponse.type)(): F[GetUsersResponse] = {
    for {
      users <- userRepo.list(0, 100)  // TODO: Implement pagination
      usersJson <- users.map(user => UserResponseJson(user.id, user.displayName)).pure[F]
    } yield respond.Ok(UsersResponseJson(usersJson.toIndexedSeq))
  }

  override def getUsersById(respond: GetUsersByIdResponse.type)(userId: Long): F[GetUsersByIdResponse] = {
    userRepo.get(userId) map {
      case None => respond.NotFound
      case Some(user) => respond.Ok(UserResponseJson(user.id, user.displayName))
    }
  }

  override def postUsers(respond: PostUsersResponse.type)(body: PostUserRequestJson): F[PostUsersResponse] = {
    userRepo.create(User(0, body.displayName)) map { user =>
      respond.Ok(UserResponseJson(user.id, user.displayName))
    }
  }

  override def patchUserById(respond: PatchUserByIdResponse.type)(userId: Long, body: PatchUserRequestJson): F[PatchUserByIdResponse] = {
    userRepo.update(User(userId, body.displayName)) map {
      case None => respond.NotFound
      case Some(user) => respond.Ok(UserResponseJson(user.id, user.displayName))
    }
  }

  override def deleteUserById(respond: DeleteUserByIdResponse.type)(userId: Long): F[DeleteUserByIdResponse] = {
    userRepo.delete(userId) map {
      case None => respond.NotFound
      case Some(_) => respond.NoContent
    }
  }
}
