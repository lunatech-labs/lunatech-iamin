package com.lunatech.xiamin.utils

//import com.lunatech.iamin.domain.users.User
//import org.scalacheck.Arbitrary.arbitrary
//import org.scalacheck._
//
//trait UserArbitraries {
//
//  implicit val user: Arbitrary[User] = Arbitrary[User] {
//    for {
//      id <- Gen.posNum[Long]
//      displayName <- arbitrary[String]
//    } yield User(id, displayName)
//  }
//}
//
//trait UsersResourceArbitraries {
//
//  implicit val request: Arbitrary[PostUsersRequest] = Arbitrary[PostUsersRequest] {
//    for {
//      displayName <- arbitrary[String]
//    } yield PostUsersRequest(displayName)
//  }
//}