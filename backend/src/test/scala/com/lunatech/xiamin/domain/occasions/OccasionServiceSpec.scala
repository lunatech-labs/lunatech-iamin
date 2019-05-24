package com.lunatech.xiamin.domain.occasions

//import java.time.LocalDate
//
//import cats.Id
//import com.lunatech.iamin.repository.InMemoryOccasionRepository
//import org.scalacheck.{Arbitrary, Gen}
//import org.scalatest.{EitherValues, FreeSpec, Matchers, OptionValues, ParallelTestExecution}
//import org.scalatest.prop.GeneratorDrivenPropertyChecks
//
//class OccasionServiceSpec
//  extends FreeSpec
//    with GeneratorDrivenPropertyChecks
//    with Matchers
//    with OptionValues
//  with EitherValues
//    with ParallelTestExecution {
//
//  private val repo = new InMemoryOccasionRepository[Id]()
//  private val service = new OccasionService[Id](repo)
//
//  lazy val knownUserId: Long = 0L
//
//  private val unknownUserIdGen = Gen.choose(1000000L, Long.MaxValue)
//  private val unknownDateGen = Gen.choose(10001L, 20000L).map(LocalDate.ofEpochDay)
//
//  private implicit val arbOccasion: Arbitrary[Occasion] = Arbitrary[Occasion] {
//    for {
//      date <- Gen.choose(0L, 10000L).map(LocalDate.ofEpochDay)
//      isPresent <- Gen.oneOf(true, false)
//    } yield Occasion(knownUserId, date, isPresent)
//  }
//
//  "occasion repository" - {
//    "should list occasions" in {
//      forAll { occasion: Occasion =>
//
//        val eitherCreated = repo.create(occasion)
//        val created = eitherCreated.right.value
//        val list = service.list(created.userId, LocalDate.MIN, LocalDate.MAX)
//
////        list should contain(created)  // Not working with cats.Id
//        list.exists(o => o.userId == created.userId && o.date == created.date && o.isPresent == created.isPresent) shouldBe true
//      }
//    }
//
//    "should not be able to get unknown occasion" in {
//      forAll(unknownUserIdGen) { unknownUserId: Long =>
//
//        val unknown = service.get(unknownUserId, LocalDate.MIN).value
//
//        unknown shouldBe None
//      }
//
//      forAll(unknownDateGen) { unknownDate: LocalDate =>
//
//        val unknown = service.get(knownUserId, unknownDate).value
//
//        unknown shouldBe None
//      }
//    }
//
//    "should not be able to create occasion with overlapping dates" in {
//      forAll { occasion: Occasion =>
//
//        val eitherCreated = service.create(occasion.userId, occasion.date, occasion.isPresent).value
//        val created = eitherCreated.right.value
//        val reCreated = service.create(occasion.userId, occasion.date, occasion.isPresent).value
//
//        created shouldBe occasion
//
//        reCreated shouldBe 'left
//        reCreated.left.value shouldBe CreateFailed.DateAlreadyTaken
//
//      }
//    }
//
//    "should be able to create and get occasion" in {
//      forAll { occasion: Occasion =>
//
//        val eitherCreated = service.create(occasion.userId, occasion.date, occasion.isPresent).value
//        val created = eitherCreated.right.value
//        val maybeRetrieved = service.get(created.userId, created.date).value
//        val retrieved = maybeRetrieved.value
//
//        created shouldBe occasion
//        retrieved shouldBe occasion
//      }
//    }
//
//    "should not be able to update unknown occasion" in {
//      forAll(arbOccasion.arbitrary, unknownUserIdGen) { (occasion: Occasion, unknownUserId: Long) =>
//
//        val eitherUpdated = service.update(unknownUserId, occasion.date, occasion.isPresent).value
//
//        eitherUpdated.left.value shouldBe UpdateFailed.UserNotFound
//      }
//    }
//
//    "should be able to update occasion" in {
//      forAll { occasion: Occasion =>
//
//        val eitherCreated = service.create(occasion.userId, occasion.date, occasion.isPresent).value
//        val created = eitherCreated.right.value
//        val eitherUpdated = service.update(occasion.userId, occasion.date, !occasion.isPresent).value
//        val updated = eitherUpdated.right.value
//
//        created shouldBe occasion
//        updated shouldBe created.copy(isPresent = !created.isPresent)
//      }
//    }
//
//    "should not be able to delete unknown occasion" in {
//      forAll(arbOccasion.arbitrary, unknownUserIdGen) { (occasion: Occasion, unknownUserId: Long) =>
//        val eitherCreated = service.create(occasion.userId, occasion.date, occasion.isPresent).value
//        val created = eitherCreated.right.value
//        val maybeDeleted = service.delete(unknownUserId, created.date).value
//
//        maybeDeleted shouldBe None
//      }
//    }
//
//    "should be to delete occasion" in {
//      forAll { occasion: Occasion =>
//        val eitherCreated = service.create(occasion.userId, occasion.date, occasion.isPresent).value
//        val created = eitherCreated.right.value
//        val maybeDeleted = service.delete(created.userId, created.date).value
//
//        maybeDeleted.value shouldBe ()
//      }
//    }
//  }
//}
