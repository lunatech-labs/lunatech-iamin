package com.lunatech.iamin.core.occasion.repository

import java.time.LocalDate

import com.lunatech.iamin.helpers.DoobieTest
import com.lunatech.iamin.{Occasion, UserId}
import doobie.scalatest.IOChecker
import org.scalatest.FunSuite

class DoobieOccasionRepositorySpec extends FunSuite with IOChecker with DoobieTest {

  import DoobieOccasionRepository._

  test("create") {
    check(SQL.insert(Occasion.Create(UserId(1), LocalDate.now, isPresent = true)))
  }

  test("select") {
    check(SQL.select(UserId(1), LocalDate.now))
  }

  test("selectMany") {
    check(SQL.selectMany(UserId(1), Some(LocalDate.now), Some(LocalDate.now)))
  }

  test("update") {
    check(SQL.update(UserId(1), LocalDate.now, Occasion.Update(true)))
  }

  test("delete") {
    check(SQL.delete(UserId(1), LocalDate.now))
  }
}

