package com.lunatech.iamin.core.user.repository

import com.lunatech.iamin.helpers.DoobieTest
import com.lunatech.iamin.{User, UserId}
import doobie.scalatest.IOChecker
import org.scalatest.FunSuite

class DoobieUserRepositorySpec extends FunSuite with IOChecker with DoobieTest {

  import DoobieUserRepository._

  test("create") {
    check(SQL.insert(User.Create("foo")))
  }

  test("select") {
    check(SQL.select(UserId(1)))
  }

  test("select many") {
    check(SQL.selectMany(UserId.Zero, 10))
  }

  test("update") {
    check(SQL.update(UserId.Zero, User.Update("bar")))
  }

  test("delete") {
    check(SQL.delete(UserId(1)))
  }
}
