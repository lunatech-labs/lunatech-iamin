package com.lunatech.xiamin.domain.users

//import cats.Id
//import com.lunatech.iamin.repository.InMemoryUserRepository
//import org.scalacheck.Gen
//import org.scalatest.prop.GeneratorDrivenPropertyChecks
//import org.scalatest._
//
//class UserServiceSpec
//  extends FreeSpec
//    with GeneratorDrivenPropertyChecks
//    with Matchers
//    with OptionValues
//    with Inspectors
//    with ParallelTestExecution {
//
//  private val repo = new InMemoryUserRepository[Id]()
//  private val service = new UserService[Id](repo)
//
//  private val unknownIdGen = Gen.choose(1000000L, Long.MaxValue)
//
//  "user service" - {
//    "should list users" in {
//      forAll { displayName: String =>
//
//        val created = service.create(displayName)
//        val list = service.list(0, Int.MaxValue)
//
////        list should contain(created)  // Not working with cats.Id
//        list.exists(u => u.id == created.id && u.name == created.name) shouldBe true
//      }
//    }
//
//    "should not be able to get unknown user" in {
//      forAll(unknownIdGen) { unknownId =>
//
//        val unknown = service.get(unknownId).value
//
//        unknown shouldBe None
//      }
//    }
//
//    "should be able to add and get user" in {
//      forAll { displayName: String =>
//
//        val created = service.create(displayName)
//        val retrieved = service.get(created.id).value
//
//        created.name shouldBe displayName
//        retrieved.value.name shouldBe displayName
//      }
//
//    }
//
//    "should not be able to update unknown user" in {
//      forAll(unknownIdGen, Gen.asciiStr) { case (unknownId: Long, displayName: String) =>
//
//        val unknown = service.update(unknownId, displayName).value
//
//        unknown shouldBe None
//      }
//    }
//
//    "should be able to update user" in {
//      forAll { displayName: String =>
//
//        val created = service.create(displayName)
//        val updated = service.update(created.id, created.name.reverse).value
//
//        updated.value.name shouldBe displayName.reverse
//        updated.value.name shouldBe created.name.reverse
//      }
//    }
//
//    "should not be able to delete unknown user" in {
//      forAll(unknownIdGen) { unknownId: Long =>
//
//        val unknown = service.delete(unknownId).value
//
//        unknown shouldBe None
//      }
//    }
//
//    "should be able to delete user" in {
//      forAll { displayName: String =>
//
//        val created = service.create(displayName)
//        val deleted = service.delete(created.id).value
//        val list = service.list(0, 100)
//
//        deleted shouldBe Some(())
////        list should not contain created // Not working with cats.Id
//        list.exists(u => u.id == created.id) shouldBe false
//      }
//    }
//  }
//}
