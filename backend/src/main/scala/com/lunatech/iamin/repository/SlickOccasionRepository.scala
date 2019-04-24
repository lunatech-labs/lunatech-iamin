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

  override def create(occasion: Occasion): F[Either[CreateFailed, Occasion]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Occasions += OccasionsRow(occasion.userId, occasion.date, occasion.isPresent)
    } redeem (
      {
        case e: PSQLException if e.getMessage contains "violates foreign key constraint" =>
          CreateFailed.UserNotFound.asLeft[Occasion]
        case e: PSQLException if e.getMessage contains "violates unique constraint" =>
          CreateFailed.DateAlreadyTaken.asLeft[Occasion]
      },
      _ => occasion.asRight[CreateFailed]
    )
  }

  override def update(occasion: Occasion): F[Either[UpdateFailed, Occasion]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Occasions
        .filter(o => o.userId === occasion.userId.bind && o.date === occasion.date.bind)
        .map(_.isPresent)
        .update(occasion.isPresent)
    } map { affectedRows =>
      if (affectedRows eqv 1) occasion.asRight[UpdateFailed] else UpdateFailed.UserNotFound.asLeft[Occasion]
    }
  }

  override def delete(userId: Long, date: LocalDate): F[Option[Unit]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Occasions
        .filter(o => o.userId === userId.bind && o.date === date.bind)
        .delete
    } map { affectedRows =>
      if (affectedRows eqv 1) ().some else none[Unit]
    }
  }

  override def get(userId: Long, date: LocalDate): F[Option[Occasion]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Occasions
        .filter(o => o.userId === userId.bind && o.date === date.bind)
        .result
        .headOption
    } map { _.map(_.transformInto[Occasion]) }
  }

  override def list(userId: Long, from: LocalDate, to: LocalDate): F[Seq[Occasion]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Occasions
        .filter { o => o.userId === userId.bind && o.date > from.bind && o.date < to.bind }
        .sortBy(_.date)
        .result
    } map { _.map(_.transformInto[Occasion]) }
  }
}
