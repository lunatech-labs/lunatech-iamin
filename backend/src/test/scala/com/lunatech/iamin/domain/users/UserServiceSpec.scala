package com.lunatech.iamin.domain.users

import cats.effect.IO
import com.lunatech.iamin.repository.InMemoryUserRepository
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FreeSpec, Matchers, OptionValues, ParallelTestExecution}

class UserServiceSpec
  extends FreeSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with OptionValues
    with ParallelTestExecution {

  private val repo = new InMemoryUserRepository[IO]()
  private val service = new UserService[IO](repo)

  private val idGen = Gen.choose(0L, 1000000L)
  private val unknownIdGen = Gen.choose(1000000L, Long.MaxValue)

  "user service" - {
    "should list users" in {
      forAll { displayName: String =>
        (for {
          created <- service.create(displayName)
          list <- service.list(0, 100)
        } yield {
          list should contain(created)
        }).unsafeRunSync()
      }
    }

    "should not be able to get unknown user" in {
      forAll(unknownIdGen) { unknownId =>
        (for {
          unknown <- service.get(unknownId).value
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }
    }

    "should be able to add and get user" in {
      forAll { displayName: String =>
        (for {
          created <- service.create(displayName)
          retrieved <- service.get(created.id).value
        } yield {
          created.displayName shouldBe displayName
          retrieved.value.displayName shouldBe displayName
        }).unsafeRunSync()
      }

    }

    "should not be able to update unknown user" in {
      forAll(unknownIdGen, Gen.asciiStr) { case (unknownId: Long, displayName: String) =>
        (for {
          unknown <- service.update(unknownId, displayName).value
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }
    }

    "should be able to update user" in {
      forAll { displayName: String =>
        (for {
          created <- service.create(displayName)
          updated <- service.update(created.id, created.displayName.reverse).value
        } yield {
          updated.value.displayName shouldBe displayName.reverse
        }).unsafeRunSync()
      }
    }

    "should not be able to delete unknown user" in {
      forAll(unknownIdGen) { unknownId: Long =>
        (for {
          unknown <- service.delete(unknownId).value
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }
    }

    "should be able to delete user" in {
      forAll { displayName: String =>
        (for {
          created <- service.create(displayName)
          deleted <- service.delete(created.id).value
          list <- service.list(0, 100)
        } yield {
          deleted shouldBe Some(())
          list should not contain created
        }).unsafeRunSync()
      }
    }
  }
}
