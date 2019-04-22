package com.lunatech.iamin.domain.users

import cats.effect.IO
import com.lunatech.iamin.utils.{DatabaseTest, UserArbitraries}
import org.scalacheck.Arbitrary._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{EitherValues, FreeSpec, Matchers}

class InMemoryUsersRepositorySpec extends UsersRepositorySpec {
  override val repo = new InMemoryUsersRepository()
}

class DatabaseUsersRepositorySpec extends UsersRepositorySpec with DatabaseTest {
  override val repo = new DatabaseUsersRepository(testDatabase)
}

trait UsersRepositorySpec
  extends FreeSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with EitherValues
    with UserArbitraries {

  val repo: UsersRepository[IO]

  s"repository" - {
    "is empty initially" in {
      (for {
        users <- repo.getUsers()
      } yield {
        users shouldBe 'empty
      }).unsafeRunSync()
    }

    "holds users" in {
      forAll { (displayName1: String, displayName2: String) =>
        (for {
          user1 <- repo.createUser(displayName1)
          user2 <- repo.createUser(displayName2)
          users <- repo.getUsers()
        } yield {
          users should contain(user1)
          users should contain(user2)
        }).unsafeRunSync()
      }
    }

    "can create user" in {
      forAll { displayName: String =>
        (for {
          user <- repo.createUser(displayName)
        } yield {
          user.displayName shouldBe displayName
        }).unsafeRunSync()
      }
    }

    "cannot get unknown user" in {
      (for {
        user <- repo.getUser(-1)
      } yield {
        user shouldBe 'left
        user.left.value shouldBe UserNotFound
      }).unsafeRunSync()
    }

    "can get known user" in {
      forAll { displayName: String =>
        (for {
          created <- repo.createUser(displayName)
          user <- repo.getUser(created.id)
        } yield {
          user shouldBe 'right
          created shouldBe user.right.value
        }).unsafeRunSync()
      }
    }

    "cannot update unknown user" in {
        (for {
          user <- repo.updateUser(User(-1, ""))
        } yield {
          user shouldBe 'left
          user.left.value shouldBe UserNotFound
        }).unsafeRunSync()
    }

    "can update user" in {
      forAll { (displayName: String, update: User) =>
        (for {
          id <- repo.createUser(displayName).map(_.id)
          updated <- repo.updateUser(update.copy(id = id))
          user    <- repo.getUser(id)
        } yield {
          user shouldBe 'right

          user.right.value.id shouldBe id
          updated shouldBe user
        }).unsafeRunSync()
      }
    }

    "cannot delete unknown user" in {
      (for {
        user <- repo.deleteUser(-1)
      } yield {
        user shouldBe 'left
        user.left.value shouldBe UserNotFound
      }).unsafeRunSync()
    }

    "can delete user" in {
      forAll { displayName: String =>
        (for {
          id      <- repo.createUser(displayName).map(_.id)
          deleted <- repo.deleteUser(id)
        } yield {
          deleted shouldBe 'right
        }).unsafeRunSync()
      }
    }
  }
}