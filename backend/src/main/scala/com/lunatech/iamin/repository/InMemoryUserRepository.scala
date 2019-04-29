package com.lunatech.iamin.repository

import java.util.concurrent.atomic.AtomicLong

import cats.Applicative
import cats.implicits._
import com.lunatech.iamin.domain.users.{User, UserRepository}

import scala.collection.concurrent.TrieMap

class InMemoryUserRepository[F[_] : Applicative] extends UserRepository[F] {

  private val cache = new TrieMap[Long, User]()
  private val ids = new AtomicLong(0L)

  /** @inheritdoc */
  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  override def create(user: User): F[User] = {
    val id = ids.incrementAndGet()
    val toSave = user.copy(id = id)
    cache.putIfAbsent(id, toSave)
    toSave.pure[F]
  }

  /** @inheritdoc */
  override def update(user: User): F[Option[User]] = {
    if (cache.contains(user.id)) {
      cache.update(user.id, user)
      user.some.pure[F]
    } else {
      none[User].pure[F]
    }
  }

  /** @inheritdoc */
  override def delete(id: Long): F[Option[Unit]] =
    cache.remove(id).map(_ => ()).pure[F]

  /** @inheritdoc */
  override def get(id: Long): F[Option[User]] =
    cache.get(id).pure[F]

  /** @inheritdoc */
  override def list(offset: Long, limit: Int): F[Seq[User]] =
    cache.values.toSeq
      .filter(_.id > offset)
      .sortBy(_.id)
      .take(limit)
      .pure[F] // TODO: Add Pagination
}
