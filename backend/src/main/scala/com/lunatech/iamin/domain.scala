package com.lunatech.iamin

import java.time.LocalDate


final case class UserId(value: Long) extends AnyRef
object UserId {
  val Zero: UserId = UserId(0L)
}

final case class User(id: UserId, name: String)
object User {
  final case class Create(name: String)
  final case class Update(name: String)
}

final case class Occasion(userId: UserId, startDate: LocalDate, isPresent: Boolean)

object Occasion {
  final case class Create(userId: UserId, date: LocalDate, isPresent: Boolean)
  final case class Update(isPresent: Boolean)

  sealed trait CreateException extends Exception
  object CreateException {
    final case class UserNotFound(userId: UserId) extends CreateException
    final case class DateAlreadyTake(date: LocalDate) extends CreateException
  }

  case object NotFound extends Exception
}