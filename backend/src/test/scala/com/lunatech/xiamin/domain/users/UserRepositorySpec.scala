package com.lunatech.xiamin.domain.users

//import cats.effect.IO
//import com.lunatech.iamin.repository.InMemoryUserRepository
//import org.scalacheck.Arbitrary.arbitrary
//import org.scalacheck.{Arbitrary, Gen}
//import org.scalatest.prop.GeneratorDrivenPropertyChecks
//import org.scalatest._
//
//class InMemoryUserRepositorySpec extends UserRepositorySpec {
//  override val repo = new InMemoryUserRepository[IO]()
//}
//
////class SlickUserRepositorySpec extends UserRepositorySpec with DatabaseTest {
////  override val repo = new SlickUserRepository[IO](testDatabase)
////}
//
//abstract class UserRepositorySpec
//  extends FreeSpec
//  with GeneratorDrivenPropertyChecks
//  with Matchers
//  with OptionValues
//  with EitherValues
//  with ParallelTestExecution {
//
//  val repo: UserRepository[IO]
//
//  private val highIdGen = Gen.choose(1000000L, Long.MaxValue)
//  private implicit val arbUser: Arbitrary[User] = Arbitrary[User] {
//    for {
//      id <- Gen.posNum[Long]
//      displayName <- arbitrary[String]
//    } yield User(id, displayName)
//  }
//
//  "user repository" - {
//    "should list users" in {
//      forAll { user: User =>
//        (for {
//          created <- repo.create(user)
//          list <- repo.list(0, Int.MaxValue)
//        } yield {
//          list should contain(created)
//        }).unsafeRunSync()
//      }
//    }
//
//    "should not be able to get unknown user" in {
//      forAll(highIdGen) { id: Long =>
//        (for {
//          unknown <- repo.get(id)
//        } yield {
//          unknown shouldBe None
//        }).unsafeRunSync()
//      }
//    }
//
//    "should be able to add and get user" in {
//      forAll { user: User =>
//        (for {
//          created <- repo.create(user)
//          retrieved <- repo.get(created.id)
//        } yield {
//          created.name shouldBe user.name
//          retrieved.value.name shouldBe user.name
//        }).unsafeRunSync()
//      }
//
//    }
//
//    "should not be able to update unknown user" in {
//      forAll { user: User =>
//        (for {
//          unknown <- repo.update(user.copy(id = highIdGen.sample.value))  // Otherwise gen might provide an existing id
//        } yield {
//          unknown shouldBe None
//        }).unsafeRunSync()
//      }
//    }
//
//    "should be able to update user" in {
//      forAll { user: User =>
//        (for {
//          created <- repo.create(user)
//          updated <- repo.update(created.copy(name = created.name.reverse))
//        } yield {
//          updated.value.name shouldBe user.name.reverse
//        }).unsafeRunSync()
//      }
//    }
//
//    "should not be able to delete unknown user" in {
//      forAll(highIdGen) { id: Long =>
//        (for {
//          unknown <- repo.delete(id)
//        } yield {
//          unknown shouldBe None
//        }).unsafeRunSync()
//      }
//    }
//
//    "should be able to delete user" in {
//      forAll { user: User =>
//        (for {
//          created <- repo.create(user)
//          deleted <- repo.delete(created.id)
//          list <- repo.list(0, 100)
//        } yield {
//          deleted shouldBe Some(())
//          list should not contain(created)
//        }).unsafeRunSync()
//      }
//    }
//  }
//}
//
