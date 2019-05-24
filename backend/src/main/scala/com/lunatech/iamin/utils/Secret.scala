package com.lunatech.iamin.utils

import tsec.common._
import tsec.hashing.jca._

final class Secret[A] private (val value: A) {
  import Secret._

  override def toString: String = s"Secret($shortHash)"

  override def equals(that: Any): Boolean =
    that match {
      case Secret(a) => value == a
      case _ => false
    }

  def copy[B](value: B = value): Secret[B] = new Secret(value = value)

  lazy val shortHash: String = value.toString.utf8Bytes.hash[SHA1].toHexString.take(ShortHashLength)
}

object Secret {

  private val ShortHashLength: Int = 7

  def apply[A](value: A): Secret[A] = new Secret(value)

  def unapply[A](secret: Secret[A]): Option[A] = Some(secret.value)
}
