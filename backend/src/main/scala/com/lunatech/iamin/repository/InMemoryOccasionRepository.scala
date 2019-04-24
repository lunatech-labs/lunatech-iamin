package com.lunatech.iamin.repository

import java.time.LocalDate

import cats.Applicative
import cats.implicits._
import com.lunatech.iamin.domain.occasions
import com.lunatech.iamin.domain.occasions.{CreateFailed, Occasion, OccasionRepository, UpdateFailed}

import scala.collection.concurrent.TrieMap

class InMemoryOccasionRepository[F[_] : Applicative] extends OccasionRepository[F] {

  private type Key = (Long, LocalDate)
  private val cache = new TrieMap[Key, Occasion]()

  @SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
  override def create(occasion: occasions.Occasion): F[Either[CreateFailed, Occasion]] = {
    Either.cond[CreateFailed, Occasion](
      !cache.contains((occasion.userId, occasion.date)),
      { cache += ((occasion.userId, occasion.date) -> occasion); occasion },
      CreateFailed.DateAlreadyTaken
    ).pure[F]
  }

  override def update(occasion: occasions.Occasion): F[Either[UpdateFailed, Occasion]] =
    Either.cond[UpdateFailed, Occasion](
      cache.contains((occasion.userId, occasion.date)),
      { cache.update((occasion.userId, occasion.date), occasion); occasion},
      UpdateFailed.UserNotFound
    ).pure[F]

  override def delete(userId: Long, date: LocalDate): F[Option[Unit]] =
    cache.remove((userId, date)).map(_ => ()).pure[F]

  override def get(userId: Long, date: LocalDate): F[Option[Occasion]] =
    cache.get((userId, date)).pure[F]

  override def list(userId: Long, from: LocalDate, to: LocalDate): F[Seq[Occasion]] =
    cache
      .filterKeys { case (id, _) => id === userId }
      .values
      .filter { occasion => occasion.date.isAfter(from) && occasion.date.isBefore(to) }
      .toSeq
      .sortBy(_.date.toEpochDay)
      .pure[F]
}
