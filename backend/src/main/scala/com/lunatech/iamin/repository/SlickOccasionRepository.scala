package com.lunatech.iamin.repository

import java.time.LocalDate

import cats.Applicative
import cats.effect.LiftIO
import cats.implicits._
import com.lunatech.iamin.database.Profile.api._
import com.lunatech.iamin.database.tables.{OccasionsRow, Tables}
import com.lunatech.iamin.domain.occasions.{CreateFailed, Occasion, OccasionRepository, UpdateFailed}
import io.scalaland.chimney.dsl._
import org.postgresql.util.PSQLException

class SlickOccasionRepository[F[_] : Applicative : LiftIO](db: Database) extends OccasionRepository[F] {

  private implicit val database: Database = db

  override def create(occasion: Occasion): F[Either[CreateFailed, Occasion]] =
    tx {
      Tables.Occasions += OccasionsRow(occasion.userId, occasion.date, None, occasion.isPresent)
    }.redeem(
      {
        case e: PSQLException if e.getMessage contains "violates foreign key constraint" =>
          CreateFailed.UserNotFound.asLeft[Occasion]
        case e: PSQLException if e.getMessage contains "violates unique constraint" =>
          CreateFailed.DateAlreadyTaken.asLeft[Occasion]
      },
      _ => occasion.asRight[CreateFailed]
    ).to[F]

  override def update(occasion: Occasion): F[Either[UpdateFailed, Occasion]] =
    tx {
      Tables.Occasions
        .filter(o => o.userId === occasion.userId.bind && o.startDate === occasion.date.bind)
        .map(_.isPresent)
        .update(occasion.isPresent)
    }.map { affectedRows =>
      if (affectedRows eqv 1) occasion.asRight[UpdateFailed] else UpdateFailed.UserNotFound.asLeft[Occasion]
    }.to[F]

  override def delete(userId: Long, date: LocalDate): F[Option[Unit]] =
    tx {
      Tables.Occasions
        .filter(o => o.userId === userId.bind && o.startDate === date.bind)
        .delete
    }.map { affectedRows =>
      if (affectedRows eqv 1) ().some else none[Unit]
    }.to[F]

  override def get(userId: Long, date: LocalDate): F[Option[Occasion]] =
    tx {
      Tables.Occasions
        .filter(o => o.userId === userId.bind && o.startDate === date.bind)
        .result
        .headOption
    }.map { _.map(occasionRowIntoOccasion) }.to[F]

  override def list(userId: Long, from: LocalDate, to: LocalDate): F[Seq[Occasion]] =
    tx {
      Tables.Occasions
        .filter { o => o.userId === userId.bind && o.startDate > from.bind && o.startDate < to.bind }
        .sortBy(_.startDate)
        .result
    }.map { _.map(occasionRowIntoOccasion) }.to[F]

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private implicit def occasionRowIntoOccasion(user: OccasionsRow): Occasion =
    user.into[Occasion]
      .withFieldComputed(_.date, o => o.startDate)
      .transform
}
