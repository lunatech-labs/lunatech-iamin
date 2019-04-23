package com.lunatech.iamin.endpoints.users

import cats.effect.Effect
import cats.implicits._
import com.lunatech.iamin.domain.users.{User, UserRepositoryAlgebra}
import com.lunatech.iamin.endpoints.definitions.{PostAvailabilityRequest, PostUsersRequest, UserResponse, UsersListResponse}

class UsersHandlerImpl[F[_] : Effect](repo: UserRepositoryAlgebra[F]) extends UsersHandler[F] {

  override def getUsers(respond: GetUsersResponse.type)(): F[GetUsersResponse] =
    for {
      users <- repo.list(0, 100)
      userResponses <- users.map(user => UserResponse(user.id, user.displayName)).pure[F]
      response <- respond.Ok(UsersListResponse(userResponses.toIndexedSeq)).pure[F]
    } yield response

  override def getUsersById(respond: GetUsersByIdResponse.type)(userId: Long): F[GetUsersByIdResponse] = {
    repo.get(userId).map {
      case None => respond.NotFound
      case Some(user) => respond.Ok(UserResponse(user.id, user.displayName))
    }
  }

  override def postUsers(respond: PostUsersResponse.type)(body: PostUsersRequest): F[PostUsersResponse] =
    for {
      createdUser <- repo.create(User(0, body.displayName))
    } yield respond.Ok(UserResponse(createdUser.id, createdUser.displayName))

  override def patchUserById(respond: PatchUserByIdResponse.type)(userId: Long, body: PostUsersRequest): F[PatchUserByIdResponse] = {
    repo.update(User(userId, body.displayName)) map {
      case None => respond.NotFound
      case Some(updatedUser) => respond.Ok(UserResponse(updatedUser.id, updatedUser.displayName))
    }
  }

  override def deleteUserById(respond: DeleteUserByIdResponse.type)(userId: Long): F[DeleteUserByIdResponse] = {
    repo.delete(userId) map {
      case None => respond.NotFound
      case Some(_) => respond.NoContent
    }
  }

  override def postUserByIdAvailabilities(respond: PostUserByIdAvailabilitiesResponse.type)(userId: Long, body: PostAvailabilityRequest): F[PostUserByIdAvailabilitiesResponse] = ???

  override def getUserByIdAvailabilities(respond: GetUserByIdAvailabilitiesResponse.type)(userId: Long): F[GetUserByIdAvailabilitiesResponse] = ???

  override def getUserByIdAvailabilitiesById(respond: GetUserByIdAvailabilitiesByIdResponse.type)(userId: Long, availabilityId: Long): F[GetUserByIdAvailabilitiesByIdResponse] = ???

  override def patchUserByIdAvailabilitiesById(respond: PatchUserByIdAvailabilitiesByIdResponse.type)(userId: Long, availabilityId: Long, body: PostAvailabilityRequest): F[PatchUserByIdAvailabilitiesByIdResponse] = ???

  override def deleteUserByIdAvailabilitiesById(respond: DeleteUserByIdAvailabilitiesByIdResponse.type)(userId: Long, availabilityId: Long): F[DeleteUserByIdAvailabilitiesByIdResponse] = ???
}
