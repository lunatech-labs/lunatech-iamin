package com.lunatech.iamin.domain

import java.time.LocalDateTime

package object users {
  final case class User(id: Long, displayName: String, created: LocalDateTime)

  case object UserNotFound
}
