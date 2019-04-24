package com.lunatech.iamin.domain

import java.time.LocalDate

package object occasions {

  final case class Occasion(
    userId: Long,
    date: LocalDate,
    isPresent: Boolean
  )

  trait CreateFailed extends Product with Serializable
  object CreateFailed {
    case object UserNotFound extends CreateFailed
    case object DateAlreadyTaken extends CreateFailed
  }

  trait UpdateFailed extends Product with Serializable
  object UpdateFailed {
    case object UserNotFound extends UpdateFailed
  }
}
