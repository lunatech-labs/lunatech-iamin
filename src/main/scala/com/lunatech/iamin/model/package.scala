package com.lunatech.iamin

import java.time.LocalDateTime

package object model {

  case class User(id: Long, displayName: String, created: LocalDateTime)

  case object UserNotFound

  case class Availability(id: Long, userId: Long, isPresent: Boolean, date: LocalDateTime)
}
