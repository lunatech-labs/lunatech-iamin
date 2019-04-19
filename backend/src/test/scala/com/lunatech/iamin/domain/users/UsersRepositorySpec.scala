package com.lunatech.iamin.domain.users

import cats.effect.IO
import com.lunatech.iamin.utils.DatabaseTest
import org.scalatest.{EitherValues, FreeSpec, Matchers}

class InMemoryUsersRepositorySpec extends UsersRepositorySpec {
  override val repo = new InMemoryUsersRepository()
}

class DatabaseUsersRepositorySpec extends UsersRepositorySpec with DatabaseTest {
  override val repo = new DatabaseUsersRepository(testDatabase)
}

trait UsersRepositorySpec extends FreeSpec with Matchers with EitherValues {

  val repo: UsersRepository[IO]

  private var user1: User = _
  private var user2: User = _

  s"repository" - {

    "is empty initially" in {
      repo.getUsers().unsafeRunSync() shouldBe 'empty
    }

    "create user" in {
      val user = repo.createUser("Alice").unsafeRunSync()

      user.displayName shouldBe "Alice"

      user1 = user
    }

    "create another user" in {
      val user = repo.createUser("Bob").unsafeRunSync()

      user.displayName shouldBe "Bob"

      user2 = user
    }

    "cannot get unknown user" in {
      repo.getUser(-1).unsafeRunSync().left.value shouldBe UserNotFound
    }

    "get user" in {
      repo.getUser(user1.id).unsafeRunSync().right.value shouldBe user1
    }

    "cannot update unknown user" in {
      repo.updateUser(user1.copy(id = -1)).unsafeRunSync().left.value shouldBe UserNotFound
    }

    "update user" in {
      val updatedUser = repo.updateUser(user1.copy(displayName = "Carol")).unsafeRunSync().right.value

      updatedUser.displayName shouldBe "Carol"

      user1 = updatedUser
    }

    "cannot delete unknown user" in {
      repo.deleteUser(-1).unsafeRunSync().left.value shouldBe UserNotFound
    }

    "delete user" in {
      repo.deleteUser(user1.id).unsafeRunSync() shouldBe 'right
    }

    "get users left" in {
      repo.getUsers().unsafeRunSync() shouldBe Seq(user2)
    }
  }
}