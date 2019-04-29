package com.lunatech.iamin.domain.occasions

import java.time.LocalDate

import cats.effect.IO
import com.lunatech.iamin.repository.InMemoryOccasionRepository
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.{EitherValues, FreeSpec, Matchers, OptionValues, ParallelTestExecution}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class OccasionServiceSpec
  extends FreeSpec
    with GeneratorDrivenPropertyChecks
    with Matchers
    with OptionValues
  with EitherValues
    with ParallelTestExecution {

  private val repo = new InMemoryOccasionRepository[IO]()
  private val service = new OccasionService[IO](repo)

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
          list <- service.list(created.userId, LocalDate.MIN, LocalDate.MAX)
        } yield {
          list should contain(created)
        }).unsafeRunSync()
      }
    }

    "should not be able to get unknown occasion" in {
      forAll(unknownUserIdGen) { unknownUserId: Long =>
        (for {
          unknown <- service.get(unknownUserId, LocalDate.MIN).value
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }

      forAll(unknownDateGen) { unknownDate: LocalDate =>
        (for {
          unknown <- service.get(knownUserId, unknownDate).value
        } yield {
          unknown shouldBe None
        }).unsafeRunSync()
      }
    }

    "should not be able to create occasion with overlapping dates" in {
      forAll { occasion: Occasion =>
        (for {
          eitherCreated <- service.create(occasion.userId, occasion.date, occasion.isPresent).value
          created <- IO.pure(eitherCreated.right.value)
          reCreated <- service.create(occasion.userId, occasion.date, occasion.isPresent).value
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
          eitherCreated <- service.create(occasion.userId, occasion.date, occasion.isPresent).value
          created <- IO.pure(eitherCreated.right.value)
          maybeRetrieved <- service.get(created.userId, created.date).value
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
          eitherUpdated <- service.update(unknownUserId, occasion.date, occasion.isPresent).value
        } yield {
          eitherUpdated.left.value shouldBe UpdateFailed.UserNotFound
        }).unsafeRunSync()
      }
    }

    "should be able to update occasion" in {
      forAll { occasion: Occasion =>
        (for {
          eitherCreated <- service.create(occasion.userId, occasion.date, occasion.isPresent).value
          created <- IO.pure(eitherCreated.right.value)
          eitherUpdated <- service.update(occasion.userId, occasion.date, !occasion.isPresent).value
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
          eitherCreated <- service.create(occasion.userId, occasion.date, occasion.isPresent).value
          created <- IO.pure(eitherCreated.right.value)
          maybeDeleted <- service.delete(unknownUserId, created.date).value
        } yield {
          maybeDeleted shouldBe None
        }).unsafeRunSync()
      }
    }

    "should be to delete occasion" in {
      forAll { occasion: Occasion =>
        (for {
          eitherCreated <- service.create(occasion.userId, occasion.date, occasion.isPresent).value
          created <- IO.pure(eitherCreated.right.value)
          maybeDeleted <- service.delete(created.userId, created.date).value
        } yield {
          maybeDeleted.value shouldBe ()
        }).unsafeRunSync()
      }
    }
  }
}
