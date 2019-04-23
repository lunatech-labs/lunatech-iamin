package com.lunatech.iamin.endpoints.users

import cats.effect.Effect
import cats.implicits._
import com.lunatech.iamin.domain.users.{User, UserRepositoryAlgebra}
import com.lunatech.iamin.endpoints.definitions.{PostAvailabilityRequest, PostUsersRequest, UserResponse, UsersListResponse}
import org.hashids.Hashids

class UsersHandlerImpl[F[_] : Effect](hashids: Hashids, repo: UserRepositoryAlgebra[F]) extends UsersHandler[F] {

  override def getUsers(respond: GetUsersResponse.type)(): F[GetUsersResponse] =
    for {
      users <- repo.list(0, 100)
      userResponses <- users.map(user => UserResponse(hashids.encode(user.id), user.displayName)).pure[F]
      response <- respond.Ok(UsersListResponse(userResponses.toIndexedSeq)).pure[F]
    } yield response

  override def getUsersById(respond: GetUsersByIdResponse.type)(userId: String): F[GetUsersByIdResponse] = {
    val id = hashids.decode(userId).head

    repo.get(id).map {
      case None => respond.NotFound
      case Some(user) => respond.Ok(UserResponse(hashids.encode(user.id), user.displayName))
    }
  }

  override def postUsers(respond: PostUsersResponse.type)(body: PostUsersRequest): F[PostUsersResponse] =
    for {
      createdUser <- repo.create(User(0, body.displayName))
    } yield respond.Ok(UserResponse(hashids.encode(createdUser.id), createdUser.displayName))

  override def patchUserById(respond: PatchUserByIdResponse.type)(userId: String, body: PostUsersRequest): F[PatchUserByIdResponse] = {
    val id = hashids.decode(userId).head

    repo.update(User(id, body.displayName)) map {
      case None => respond.NotFound
      case Some(updatedUser) => respond.Ok(UserResponse(hashids.encode(updatedUser.id), updatedUser.displayName))
    }
  }

  override def deleteUserById(respond: DeleteUserByIdResponse.type)(userId: String): F[DeleteUserByIdResponse] = {
    val id = hashids.decode(userId).head

    repo.delete(id) map {
      case None => respond.NotFound
      case Some(_) => respond.NoContent
    }
  }

  override def postUserByIdAvailabilities(respond: PostUserByIdAvailabilitiesResponse.type)(userId: String, body: PostAvailabilityRequest): F[PostUserByIdAvailabilitiesResponse] = ???

  override def getUserByIdAvailabilities(respond: GetUserByIdAvailabilitiesResponse.type)(userId: String): F[GetUserByIdAvailabilitiesResponse] = ???

  override def getUserByIdAvailabilitiesById(respond: GetUserByIdAvailabilitiesByIdResponse.type)(userId: String, availabilityId: String): F[GetUserByIdAvailabilitiesByIdResponse] = ???

  override def patchUserByIdAvailabilitiesById(respond: PatchUserByIdAvailabilitiesByIdResponse.type)(userId: String, availabilityId: String, body: PostAvailabilityRequest): F[PatchUserByIdAvailabilitiesByIdResponse] = ???

  override def deleteUserByIdAvailabilitiesById(respond: DeleteUserByIdAvailabilitiesByIdResponse.type)(userId: String, availabilityId: String): F[DeleteUserByIdAvailabilitiesByIdResponse] = ???
}
