package com.lunatech.iamin

import com.lunatech.iamin.domain.users.User
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck._

trait UserArbitratries {

  implicit val user: Arbitrary[User] = Arbitrary[User] {
    for {
      id <- Gen.posNum[Long]
      displayName <- arbitrary[String]
    } yield User(id, displayName)
  }

}
