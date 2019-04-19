package com.lunatech.iamin.domain.users

import java.time.LocalDateTime

import cats.effect.{Async, IO}
import com.lunatech.iamin.rest.definitions._
import com.lunatech.iamin.rest.users._
import org.hashids.Hashids

class UsersHandlerImpl[F[_] : Async](hashids: Hashids, usersRepository: UsersRepository[IO]) extends UsersHandler[F] {

  override def getUsers(respond: GetUsersResponse.type)(): F[GetUsersResponse] = implicitly[Async[F]].liftIO {
    for {
      users         <- usersRepository.getUsers()
      userResponses <- IO.pure(users.map(u => UserResponse(hashids.encode(u.id), u.displayName)).toIndexedSeq)
      response      <- IO.pure(respond.Ok(UsersListResponse(userResponses)))
    } yield response
  }

  override def postUsers(respond: PostUsersResponse.type)(body: PostUsersRequest): F[PostUsersResponse] =
    implicitly[Async[F]].liftIO {
      for {
        newUser  <- usersRepository.createUser(body.displayName)
        response <- IO.pure(respond.Ok(UserResponse(hashids.encode(newUser.id), newUser.displayName)))
      } yield response
    }

  override def getUsersById(respond: GetUsersByIdResponse.type)(userId: String): F[GetUsersByIdResponse] =
    implicitly[Async[F]].liftIO {
      hashids.decode(userId).headOption
        .fold {
          IO.pure[GetUsersByIdResponse](respond.NotFound)
        } { id =>
          usersRepository.getUser(id).map {
            case Left(_)     => respond.NotFound
            case Right(user) => respond.Ok(UserResponse(hashids.encode(user.id), user.displayName))
          }
        }
    }

  @SuppressWarnings(Array("org.wartremover.warts.Product", "org.wartremover.warts.Serializable"))
  override def patchUserById(respond: PatchUserByIdResponse.type)(userId: String, body: PostUsersRequest): F[PatchUserByIdResponse] =
    implicitly[Async[F]].liftIO {
      hashids.decode(userId).headOption
        .fold {
          IO.pure[PatchUserByIdResponse](respond.NotFound)
        } { id =>
          for {
            now                   <- IO(LocalDateTime.now)
            updatedUserOrNotFound <- usersRepository.updateUser(User(id, body.displayName, now))
            response              <- updatedUserOrNotFound
              .fold(
                _ => IO.pure(respond.NotFound),
                user => IO.pure(respond.Ok(UserResponse(hashids.encode(user.id), user.displayName)))
              )
          } yield response
        }
    }

  override def deleteUserById(respond: DeleteUserByIdResponse.type)(userId: String): F[DeleteUserByIdResponse] =
    implicitly[Async[F]].liftIO {
      hashids.decode(userId).headOption
        .fold {
          IO.pure[DeleteUserByIdResponse](respond.NotFound)
        } { id =>
          usersRepository.deleteUser(id).map {
            case Left(_)  => respond.NotFound
            case Right(_) => respond.NoContent
          }
        }
    }

  override def getUserByIdAvailabilities(respond: GetUserByIdAvailabilitiesResponse.type)(userId: String): F[GetUserByIdAvailabilitiesResponse] = ???

  override def postUserByIdAvailabilities(respond: PostUserByIdAvailabilitiesResponse.type)(userId: String, body: PostAvailabilityRequest): F[PostUserByIdAvailabilitiesResponse] = ???

  override def getUserByIdAvailabilitiesById(respond: GetUserByIdAvailabilitiesByIdResponse.type)(userId: String, availabilityId: String): F[GetUserByIdAvailabilitiesByIdResponse] = ???

  override def patchUserByIdAvailabilitiesById(respond: PatchUserByIdAvailabilitiesByIdResponse.type)(userId: String, availabilityId: String, body: PostAvailabilityRequest): F[PatchUserByIdAvailabilitiesByIdResponse] = ???

  override def deleteUserByIdAvailabilitiesById(respond: DeleteUserByIdAvailabilitiesByIdResponse.type)(userId: String, availabilityId: String): F[DeleteUserByIdAvailabilitiesByIdResponse] = ???
}
