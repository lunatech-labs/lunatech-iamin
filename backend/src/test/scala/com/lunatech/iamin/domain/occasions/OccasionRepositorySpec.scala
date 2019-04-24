package com.lunatech.iamin.domain.occasions

import java.time.LocalDate

import cats.effect.IO
import com.lunatech.iamin.repository.{InMemoryOccasionRepository, SlickOccasionRepository, SlickUserRepository}
import com.lunatech.iamin.utils.DatabaseTest
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest._

class InMemoryOccasionRepositorySpec extends OccasionRepositorySpec {
  override val repo = new InMemoryOccasionRepository[IO]()
}

class SlickOccasionRepositorySpec extends OccasionRepositorySpec with DatabaseTest {

  override lazy val knownUserId: Long = {
    val userRepo = new SlickUserRepository[IO](testDatabase)
    val id = userRepo.create(com.lunatech.iamin.domain.users.User(0, "test user")).unsafeRunSync().id
    id
  }

  override val repo = new SlickOccasionRepository[IO](testDatabase)
}

abstract class OccasionRepositorySpec
  extends FreeSpec
  with GeneratorDrivenPropertyChecks
  with Matchers
  with OptionValues
  with EitherValues
  with ParallelTestExecution {

  val repo: OccasionRepository[IO]

  lazy val knownUserId: Long = 0L

  private val unknownUserIdGen = Gen.choose(1000000L, Long.MaxValue)
  private val unknownDateGen = Gen.choose(10001L, 20000L).map(LocalDate.ofEpochDay)

  private implicit val arbOccasion: Arbitrary[Occasion] = Arbitrary[Occasion] {
    for {
      date <- Gen.choose(0L, 10000L).map(LocalDate.ofEpochDay)
      isPresent <- Gen.oneOf(true, false)
    } yield Occasion(knownUserId, date, isPresent)
  }

  "occasion repository" - {
    "should list occasions" in {
      forAll { occasion: Occasion =>
        (for {
          eitherCreated <- repo.create(occasion)
          created <- IO.pure(eitherCreated.right.value)
          list <- repo.list(created.userId, LocalDate.MIN, LocalDate.MAX)
        } yield {
          list should contain(created)
        }).unsafeRunSync()
      }
    }

    "should not be able to get unknown occasion" in {
      forAll(unknownUserIdGen) { unknownUserId: Long =>
        (for {
          unknown <- repo.get(unknownUserId, LocalDate.MIN)
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }

      forAll(unknownDateGen) { unknownDate: LocalDate =>
        (for {
          unknown <- repo.get(knownUserId, unknownDate)
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }
    }

    "should not be able to create occasion with overlapping dates" in {
      forAll { occasion: Occasion =>
        (for {
          eitherCreated <- repo.create(occasion)
          created <- IO.pure(eitherCreated.right.value)
          reCreated <- repo.create(occasion)
        } yield {
          created shouldBe occasion

          reCreated shouldBe 'left
          reCreated.left.value shouldBe CreateFailed.DateAlreadyTaken
        }).unsafeRunSync()
      }
    }

    "should be able to create and get occasion" in {
      forAll { occasion: Occasion =>
        (for {
          eitherCreated <- repo.create(occasion)
          created <- IO.pure(eitherCreated.right.value)
          maybeRetrieved <- repo.get(created.userId, created.date)
          retrieved <- IO.pure(maybeRetrieved.value)
        } yield {
          created shouldBe occasion
          retrieved shouldBe occasion
        }).unsafeRunSync()
      }
    }

    "should not be able to update unknown occasion" in {
      forAll(arbOccasion.arbitrary, unknownUserIdGen) { (occasion: Occasion, unknownUserId: Long) =>
        (for {
          eitherUpdated <- repo.update(occasion.copy(userId = unknownUserId))
        } yield {
          eitherUpdated.left.value shouldBe UpdateFailed.UserNotFound
        }).unsafeRunSync()
      }
    }

    "should be able to update occasion" in {
      forAll { occasion: Occasion =>
        (for {
          eitherCreated <- repo.create(occasion)
          created <- IO.pure(eitherCreated.right.value)
          eitherUpdated <- repo.update(created.copy(isPresent = !created.isPresent))
          updated <- IO.pure(eitherUpdated.right.value)
        } yield {
          created shouldBe occasion
          updated shouldBe created.copy(isPresent = !created.isPresent)
        }).unsafeRunSync()
      }
    }

    "should not be able to delete unknown occasion" in {
      forAll(arbOccasion.arbitrary, unknownUserIdGen) { (occasion: Occasion, unknownUserId: Long) =>
        (for {
          eitherCreated <- repo.create(occasion)
          created <- IO.pure(eitherCreated.right.value)
          maybeDeleted <- repo.delete(unknownUserId, created.date)
        } yield {
          maybeDeleted shouldBe None
        }).unsafeRunSync()
      }
    }

    "should be to delete unknown occasion" in {
      forAll { occasion: Occasion =>
        (for {
          eitherCreated <- repo.create(occasion)
          created <- IO.pure(eitherCreated.right.value)
          maybeDeleted <- repo.delete(created.userId, created.date)
        } yield {
          maybeDeleted.value shouldBe ()
        }).unsafeRunSync()
      }
    }
  }
}
