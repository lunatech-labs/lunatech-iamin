package com.lunatech.iamin.domain

package object users {
  final case class User(id: Long, displayName: String)

  case object UserNotFound
}
