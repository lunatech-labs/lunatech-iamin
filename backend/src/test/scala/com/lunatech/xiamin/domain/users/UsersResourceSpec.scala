package com.lunatech.xiamin.domain.users

//import cats.effect.IO
//import com.lunatech.iamin.endpoints.users.UsersResource
//import com.lunatech.iamin.utils.UsersResourceArbitraries
//import org.http4s.circe._
//import org.http4s.dsl.Http4sDsl
//import org.http4s.{Method, Request, Status, Uri, _}
//import org.scalacheck.Arbitrary._
//import org.scalatest.prop.GeneratorDrivenPropertyChecks
//import org.scalatest.{EitherValues, FreeSpec, Matchers}
//
//class UsersResourceSpec
//  extends FreeSpec
//    with UsersResourceArbitraries
//    with GeneratorDrivenPropertyChecks
//    with Matchers
//    with EitherValues
//    with Http4sDsl[IO] {
//
//  private val hashids = new Hashids("secret")
//  private val repo = new InMemoryUsersRepository()
//  private val resource = new UsersResource[IO].routes(new UsersHandlerImpl[IO](hashids, repo))
//
//  implicit private val postUsersRequestEnc: EntityEncoder[IO, PostUsersRequest]= jsonEncoderOf
//  implicit private val userResponseDec: EntityDecoder[IO, UserResponse] = jsonOf
//  implicit private val usersListResponseDec: EntityDecoder[IO, UsersListResponse] = jsonOf
//
//  "users resource" - {
//
//    "GET /users should" - {
//      "return an empty list initially" in {
//        (for {
//           getUsersResponse <- resource.orNotFound(Request[IO](Method.GET, Uri.uri("/users")))
//           usersList        <- getUsersResponse.as[UsersListResponse]
//         } yield {
//           getUsersResponse.status shouldBe Status.Ok
//
//           usersList shouldBe UsersListResponse(IndexedSeq.empty[UserResponse])
//         }).unsafeRunSync()
//       }
//
//      "return users when available" in {
//        forAll { postUserRequest: PostUsersRequest =>
//          (for {
//            postUserResponse <- resource.orNotFound(Request[IO](Method.POST, Uri.uri("/users")).withEntity(postUserRequest))
//            user             <- postUserResponse.as[UserResponse]
//            getUsersResponse <- resource.orNotFound(Request[IO](Method.GET, Uri.uri("/users")))
//            usersList        <- getUsersResponse.as[UsersListResponse]
//          } yield {
//            postUserResponse.status shouldBe Status.Ok
//
//            getUsersResponse.status shouldBe Status.Ok
//
//            usersList.items should contain(user)
//          }).unsafeRunSync()
//        }
//      }
//    }
//
//    "POST /users should" - {
//      "create users" in {
//        forAll { postUserRequest: PostUsersRequest =>
//          (for {
//            postUserResponse <- resource.orNotFound(Request[IO](Method.POST, Uri.uri("/users")).withEntity(postUserRequest))
//            createdUser      <- postUserResponse.as[UserResponse]
//            getUserResponse  <- resource.orNotFound(Request[IO](Method.GET, Uri.unsafeFromString(s"/users/${createdUser.id}")))
//            retrievedUser    <- getUserResponse.as[UserResponse]
//          } yield {
//            postUserResponse.status shouldBe Status.Ok
//
//            getUserResponse.status shouldBe Status.Ok
//
//            retrievedUser shouldBe createdUser
//          }).unsafeRunSync()
//        }
//      }
//    }
//
//    "GET /users/:id should" - {
//      "not return unknown user" in {
//        (for {
//          getUserResponse <- resource.orNotFound(Request[IO](Method.GET, Uri.uri("/users/f00b45")))
//        } yield {
//          getUserResponse.status shouldBe Status.NotFound
//        }).unsafeRunSync()
//      }
//
//      "return user" in {
//        forAll { postUserRequest: PostUsersRequest =>
//          (for {
//            postUserResponse <- resource.orNotFound(Request[IO](Method.POST, Uri.uri("/users")).withEntity(postUserRequest))
//            createdUser      <- postUserResponse.as[UserResponse]
//            getUserResponse  <- resource.orNotFound(Request[IO](Method.GET, Uri.unsafeFromString(s"/users/${createdUser.id}")))
//            retrievedUser    <- getUserResponse.as[UserResponse]
//          } yield {
//            postUserResponse.status shouldBe Status.Ok
//
//            getUserResponse.status shouldBe Status.Ok
//
//            retrievedUser shouldBe createdUser
//          }).unsafeRunSync()
//        }
//      }
//    }
//
//    "PATCH /users/:id should" - {
//      "not update unknown user" in {
//        (for {
//          patchUserResponse <- resource.orNotFound(Request[IO](Method.PATCH, Uri.uri("/users/f00b45")).withEntity(PostUsersRequest("")))
//        } yield {
//          patchUserResponse.status shouldBe Status.NotFound
//        }).unsafeRunSync()
//      }
//
//      "update user" in {
//        forAll { (postUserRequest: PostUsersRequest, newDisplayName: String) =>
//          (for {
//            postUserResponse  <- resource.orNotFound(Request[IO](Method.POST, Uri.uri("/users")).withEntity(postUserRequest))
//            createdUser       <- postUserResponse.as[UserResponse]
//            patchUserResponse <- resource.orNotFound(Request[IO](Method.PATCH, Uri.unsafeFromString(s"/users/${createdUser.id}")).withEntity(PostUsersRequest(newDisplayName)))
//            updatedUser       <- patchUserResponse.as[UserResponse]
//            getUserResponse   <- resource.orNotFound(Request[IO](Method.GET, Uri.unsafeFromString(s"/users/${createdUser.id}")))
//            retrievedUser     <- getUserResponse.as[UserResponse]
//          } yield {
//            postUserResponse.status shouldBe Status.Ok
//
//            patchUserResponse.status shouldBe Status.Ok
//
//            updatedUser shouldBe createdUser.copy(displayName = newDisplayName)
//
//            updatedUser shouldBe retrievedUser
//          }).unsafeRunSync()
//        }
//      }
//    }
//
//    "DELETE /users/:id should" - {
//      "not delete unknown user" in {
//        (for {
//          deleteUserResponse <- resource.orNotFound(Request[IO](Method.DELETE, Uri.uri("/users/f00b45")).withEntity(PostUsersRequest("")))
//        } yield {
//          deleteUserResponse.status shouldBe Status.NotFound
//        }).unsafeRunSync()
//      }
//
//      "delete user" in {
//        forAll { postUserRequest: PostUsersRequest =>
//          (for {
//            postUserResponse   <- resource.orNotFound(Request[IO](Method.POST, Uri.uri("/users")).withEntity(postUserRequest))
//            createdUser        <- postUserResponse.as[UserResponse]
//            deleteUserResponse <- resource.orNotFound(Request[IO](Method.DELETE, Uri.unsafeFromString(s"/users/${createdUser.id}")))
//            getUserResponse    <- resource.orNotFound(Request[IO](Method.GET, Uri.unsafeFromString(s"/users/${createdUser.id}")))
//          } yield {
//            postUserResponse.status shouldBe Status.Ok
//
//            deleteUserResponse.status shouldBe Status.NoContent
//
//            getUserResponse.status shouldBe Status.NotFound
//          }).unsafeRunSync()
//        }
//      }
//    }
//  }
//}
