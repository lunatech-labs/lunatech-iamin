package com.lunatech.iamin.core.occasion.repository

import java.time.LocalDate

import cats.implicits._
import com.lunatech.iamin.{Occasion, UserId}
import doobie._
import doobie.free.connection
import doobie.implicits._
import org.postgresql.util.PSQLException
import scalaz.zio.interop.catz._
import scalaz.zio.{Task, ZIO}

trait DoobieOccasionRepository extends OccasionRepository {
  import DoobieOccasionRepository._

  protected def xa: Transactor[Task]

  override def occasionRepository: OccasionRepository.Service[Any] = new OccasionRepository.Service[Any] {

    override def createOccasion(create: Occasion.Create): ZIO[Any, Occasion.CreateException, Occasion] = {
      SQL
        .insert(create)
        .run
        .map(_ => Occasion(create.userId, create.date, create.isPresent))
        .transact(xa)
        .mapError {
          case e: PSQLException if e.getMessage contains "violates foreign key constraint" =>
            Occasion.CreateException.UserNotFound(create.userId)
          case e: PSQLException if e.getMessage contains "violates unique constraint" =>
            Occasion.CreateException.DateAlreadyTake(create.date)
        }
    }

    override def getOccasion(userId: UserId, date: LocalDate): ZIO[Any, Occasion.NotFound.type, Occasion] = {
      SQL
        .select(userId, date)
        .option
        .transact(xa)
        .flatMap {
          case None           => ZIO.fail(Occasion.NotFound)
          case Some(occasion) => ZIO.succeed(occasion)
        }
        .mapError {
          _ => Occasion.NotFound
        }
    }

    override def listOccasions(userId: UserId, from: Option[LocalDate], to: Option[LocalDate]): ZIO[Any, Occasion.NotFound.type, List[Occasion]] = {
      SQL
        .selectMany(userId, from, to)
        .to[List]
        .transact(xa)
        .flatMap {
          case Nil                       => ZIO.fail(Occasion.NotFound)
          case occasions: List[Occasion] => ZIO.succeed(occasions)
        }
        .mapError {
          _ => Occasion.NotFound
        }
    }

    override def updateOccasion(userId: UserId, date: LocalDate, update: Occasion.Update): ZIO[Any, Occasion.NotFound.type, Occasion] = {
      (for {
        prevOccasion <- SQL.select(userId, date).option
        maybeUpdated  = prevOccasion.map(occasion => occasion.copy(isPresent = update.isPresent))
        _            <- maybeUpdated.fold(connection.unit)(_ => SQL.update(userId, date, update).run.void)
      } yield maybeUpdated)
        .transact(xa)
        .flatMap {
          case None           => ZIO.fail(Occasion.NotFound)
          case Some(occasion) => ZIO.succeed(occasion)
        }
        .mapError {
          _ => Occasion.NotFound
        }
    }

    override def deleteOccasion(userId: UserId, date: LocalDate): ZIO[Any, Occasion.NotFound.type, Unit] = {
      SQL.delete(userId, date)
        .run
        .transact(xa)
        .flatMap {
          case 0 => ZIO.fail(Occasion.NotFound)
          case 1 => ZIO.unit
        }
        .mapError {
          _ => Occasion.NotFound
        }
    }
  }
}

object DoobieOccasionRepository {

  object SQL {

    def insert(create: Occasion.Create): Update0 =
      sql"""
        INSERT INTO occasions (user_id, start_date, is_present)
        VALUES (${create.userId.value}, ${create.date}, ${create.isPresent})
      """.update

    def select(userId: UserId, date: LocalDate): Query0[Occasion] =
      sql"""
        SELECT user_id, start_date, is_present
        FROM occasions
        WHERE
          user_id = ${userId.value} AND
          start_date = ${date}
      """
        .query[Occasion]

    def selectMany(userId: UserId, maybeFrom: Option[LocalDate], maybeTo: Option[LocalDate]): Query0[Occasion] = {

      val from = maybeFrom.getOrElse(LocalDate.ofYearDay(1,1 ))
      val to = maybeTo.getOrElse(LocalDate.ofYearDay(9999, 1))

      sql"""
        SELECT user_id, start_date, is_present
        FROM occasions
        WHERE
          user_id = ${userId.value} AND
          start_date > ${from} AND
          start_date < ${to}
        ORDER BY start_date ASC
      """.query[Occasion]
    }

    def update(userId: UserId, date: LocalDate, update: Occasion.Update): Update0 =
      sql"""
        UPDATE occasions
        SET is_present = ${update.isPresent}
        WHERE
          user_id = ${userId.value} AND
          start_date = ${date}
      """.update

    def delete(userId: UserId, date: LocalDate): Update0 =
      sql"""
        DELETE FROM occasions
        WHERE
          user_id = ${userId.value} AND
          start_date = ${date}
      """.update

  }
}