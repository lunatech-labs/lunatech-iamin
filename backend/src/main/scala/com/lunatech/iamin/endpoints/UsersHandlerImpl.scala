package com.lunatech.iamin.endpoints

import cats.effect.Async
import cats.implicits._
import com.lunatech.iamin.domain.users.{User, UserService}
import com.lunatech.iamin.endpoints.definitions.{PatchUserRequestJson, PostUserRequestJson, UserResponseJson, UsersResponseJson}
import com.lunatech.iamin.endpoints.users._
import com.lunatech.iamin.utils.IdObfuscator
import io.scalaland.chimney.dsl._

class UsersHandlerImpl[F[_] : Async](service: UserService[F], obs: IdObfuscator) extends UsersHandler[F] {

  override def getUsers(respond: GetUsersResponse.type)(maybeObfuscatedOffsetId: Option[String] = None, maybeLimit: Option[Int] = Option(10)): F[GetUsersResponse] = {
    val offsetId = maybeObfuscatedOffsetId map obs.deobfuscate getOrElse 0L
    val limit = maybeLimit getOrElse 10

    service.list(offsetId, limit) map { users =>
      UsersResponseJson(users.map(userIntoUserResponseJson).toIndexedSeq)
    } map {
      respond.Ok
    }
  }

  override def getUsersById(respond: GetUsersByIdResponse.type)(obfuscatedUserId: String): F[GetUsersByIdResponse] = {
    val userId = obs deobfuscate obfuscatedUserId
    service.get(userId).fold[GetUsersByIdResponse](respond.NotFound)(respond.Ok(_))
  }

  override def postUsers(respond: PostUsersResponse.type)(body: PostUserRequestJson): F[PostUsersResponse] = {
    service.create(body.displayName).map(respond.Ok(_))
  }

  override def patchUserById(respond: PatchUserByIdResponse.type)(obfuscatedUserId: String, body: PatchUserRequestJson): F[PatchUserByIdResponse] = {
    val userId = obs deobfuscate obfuscatedUserId
    service.update(userId, body.displayName).fold[PatchUserByIdResponse](respond.NotFound)(respond.Ok(_))
  }

  override def deleteUserById(respond: DeleteUserByIdResponse.type)(obfuscatedUserId: String): F[DeleteUserByIdResponse] = {
    val userId = obs deobfuscate obfuscatedUserId
    service.delete(userId).fold[DeleteUserByIdResponse](respond.NotFound)(_ => respond.NoContent)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private implicit def userIntoUserResponseJson(user: User): UserResponseJson =
    user.into[UserResponseJson]
      .withFieldComputed[String, String](_.id, u => obs.obfuscate(u.id))
      .transform
//    .transformInto[UserResponseJson]
}
