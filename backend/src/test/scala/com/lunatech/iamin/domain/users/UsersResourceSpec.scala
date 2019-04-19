package com.lunatech.iamin.domain.users

import cats.effect.IO
import com.lunatech.iamin.rest.definitions.UsersListResponse._
import com.lunatech.iamin.rest.definitions.{PostUsersRequest, UserResponse, UsersListResponse}
import com.lunatech.iamin.rest.users.UsersResource
import io.circe.Json
import io.circe.syntax._
import org.hashids.Hashids
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.{Method, Request, Response, Status, Uri}
import org.scalatest.{EitherValues, FreeSpec, Matchers}

class UsersResourceSpec extends FreeSpec with Matchers with EitherValues {

  private val hashids = new Hashids("secret")
  private val repo = new InMemoryUsersRepository()

  private val resource = new UsersResource[IO].routes(new UsersHandlerImpl[IO](hashids, repo))

  private var user1: UserResponse = _
  private var user2: UserResponse = _

  "users resource" - {

    "GET /users should" - {
       "return an empty list initially" in {
         val response = serve(Request[IO](Method.GET, Uri.uri("/users")))

         response.status shouldBe Status.Ok
         response.as[Json].unsafeRunSync() shouldBe UsersListResponse(IndexedSeq.empty[UserResponse]).asJson
       }
    }

    "POST /users should" - {
      "create user1" in {
        val response = serve(Request[IO](Method.POST, Uri.uri("/users")).withEntity(PostUsersRequest("Alice").asJson))

        response.status shouldBe Status.Ok

        val user = response.as[Json].unsafeRunSync().as[UserResponse].right.value

        user.displayName shouldBe "Alice"

        user1 = user
      }

      "create user2" in {
        val response = serve(Request[IO](Method.POST, Uri.uri("/users")).withEntity(PostUsersRequest("Bob").asJson))

        response.status shouldBe Status.Ok

        val user = response.as[Json].unsafeRunSync().as[UserResponse].right.value

        user.displayName shouldBe "Bob"

        user2 = user
      }
    }

    "GET /users/:id should" - {
      "not return unknown user" in {
        val response = serve(Request[IO](Method.GET, Uri.uri("/users/f00b45")))

        response.status shouldBe Status.NotFound
      }

      "return user" in {
        val response = serve(Request[IO](Method.GET, Uri.unsafeFromString(s"/users/${user1.id}")))

        response.status shouldBe Status.Ok

        val user = response.as[Json].unsafeRunSync().as[UserResponse].right.value

        user.displayName shouldBe "Alice"
      }
    }

    "PATCH /users/:id should" - {
      "not update unknown user" in {
        val response = serve(Request[IO](Method.PATCH, Uri.uri("/users/f00b45")).withEntity(PostUsersRequest("Carol").asJson))

        response.status shouldBe Status.NotFound
      }

      "update user" in {
        val response = serve(Request[IO](Method.PATCH, Uri.unsafeFromString(s"/users/${user1.id}")).withEntity(PostUsersRequest("Carol").asJson))

        response.status shouldBe Status.Ok

        val user = response.as[Json].unsafeRunSync().as[UserResponse].right.value

        user.id shouldBe user1.id
        user.displayName shouldBe "Carol"
      }
    }

    "DELETE /users/:id should" - {
      "not delete unknown user" in {
        val response = serve(Request[IO](Method.DELETE, Uri.uri("/users/f00b45")))

        response.status shouldBe Status.NotFound
      }

      "delete user" in {
        val response = serve(Request[IO](Method.DELETE, Uri.unsafeFromString(s"/users/${user1.id}")))

        response.status shouldBe Status.NoContent
      }
    }

    "GET /users should" - {
      "return user left" in {
        val response = serve(Request[IO](Method.GET, Uri.uri("/users")))

        response.status shouldBe Status.Ok
        response.as[Json].unsafeRunSync() shouldBe UsersListResponse(IndexedSeq(user2)).asJson
      }
    }
  }

  private def serve(request: Request[IO]): Response[IO] = {
    resource.orNotFound(request).unsafeRunSync()
  }
}
