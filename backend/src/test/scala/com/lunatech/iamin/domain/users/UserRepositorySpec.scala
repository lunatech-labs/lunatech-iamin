package com.lunatech.iamin.domain.users

import cats.effect.IO
import com.lunatech.iamin.UserArbitratries
import com.lunatech.iamin.repository.inmemory.UserRepositoryInMemoryInterpreter
import com.lunatech.iamin.repository.slick.UserRepositorySlickInterpreter
import com.lunatech.iamin.utils.DatabaseTest
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers, OptionValues}

class UserRepositoryInMemoryInterpreterSpec extends UserRepositorySpec {
  override val repo = new UserRepositoryInMemoryInterpreter[IO]()
}

class UserRepositorySlickInterpreterSpec extends UserRepositorySpec with DatabaseTest {
  override val repo = new UserRepositorySlickInterpreter[IO](testDatabase)
}

abstract class UserRepositorySpec
  extends FreeSpec
  with GeneratorDrivenPropertyChecks
  with UserArbitratries
  with Matchers
  with OptionValues {

  private val highIdGen = Gen.choose(1000000L, Long.MaxValue)

  val repo: UserRepositoryAlgebra[IO]

  "user repository" - {
    "is empty initially" in {
      (for {
        users <- repo.list(0, Int.MaxValue)
      } yield {
        users shouldBe 'empty
      }).unsafeRunSync()
    }

    "should list users" in {
      forAll { user: User =>
        (for {
          created <- repo.create(user)
          list <- repo.list(0, 100)
        } yield {
          list should contain(created)
        }).unsafeRunSync()
      }
    }

    "should not be able to get unknown user" in {
      forAll(highIdGen) { id: Long =>
        (for {
          unknown <- repo.get(id)
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }
    }

    "should be able to add and get user" in {
      forAll { user: User =>
        (for {
          created <- repo.create(user)
        } yield {
          created.displayName shouldBe user.displayName
        }).unsafeRunSync()
      }

    }

    "should not be able to update unknown user" in {
      forAll { user: User =>
        (for {
          unknown <- repo.update(user.copy(id = highIdGen.sample.value))  // Otherwise gen might provide an existing id
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }
    }

    "should be able to update user" in {
      forAll { user: User =>
        for {
          created <- repo.create(user)
          updated <- repo.update(created.copy(displayName = created.displayName.reverse))
        } yield {
          updated.value.displayName shouldBe user.displayName.reverse
        }
      }
    }

    "should not be able to delete unknown user" in {
      forAll(highIdGen) { id: Long =>
        (for {
          unknown <- repo.delete(id)
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }
    }

    "should be able to delete user" in {
      forAll { user: User =>
        (for {
          created <- repo.create(user)
          deleted <- repo.delete(created.id)
        } yield {
          deleted shouldBe Some(())
        }).unsafeRunSync()
      }
    }
  }
}

