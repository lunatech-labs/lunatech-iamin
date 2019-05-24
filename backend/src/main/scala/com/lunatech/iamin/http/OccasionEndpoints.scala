package com.lunatech.iamin.http

import java.time.LocalDate

import com.lunatech.iamin.endpoints.definitions.{OccasionResponseJson, OccasionsResponseJson, PatchOccasionRequestJson, PostOccasionRequestJson}
import com.lunatech.iamin.endpoints.occasions._
import com.lunatech.iamin.Occasion.CreateException
import com.lunatech.iamin.core.idcodec.service._
import com.lunatech.iamin.core.occasion.repository._
import com.lunatech.iamin.utils.guardrailhacks
import com.lunatech.iamin.{Occasion, UserId}
import org.http4s.HttpRoutes
import scalaz.zio.interop.catz._
import scalaz.zio.{TaskR, ZIO}

class OccasionEndpoints[R <: OccasionRepository with IdCodec] {

  type OccasionEndpointTask[A] = TaskR[R, A]

  def routes: HttpRoutes[OccasionEndpointTask] = new OccasionsResource[OccasionEndpointTask].routes(new OccasionsHandlerImpl)

  private class OccasionsHandlerImpl extends OccasionsHandler[OccasionEndpointTask] {

    override def postOccasions(respond: PostOccasionsResponse.type)(hashedUserId: String, body: PostOccasionRequestJson): OccasionEndpointTask[PostOccasionsResponse] = {
      (for {
        userId <- decodeId(hashedUserId).fold(_ => UserId.Zero, _.fold(UserId.Zero)(UserId(_)))
        createdOccasion <- createOccasion(Occasion.Create(userId, body.date, body.isPresent))
      } yield createdOccasion)
        .fold(
          {
            case _: CreateException.UserNotFound    => respond.NotFound(notFoundProblem(s"user $hashedUserId was not found"))
            case _: CreateException.DateAlreadyTake => respond.Conflict(conflictProblem(s"date ${body.date} is already taken"))
          },
          occasion => respond.Ok(OccasionResponseJson(occasion.startDate, occasion.isPresent))
        )
    }

    override def getOccasions(respond: GetOccasionsResponse.type)(hashedUserId: String, from: Option[guardrailhacks.FromLocalDate_Hack], to: Option[guardrailhacks.ToLocalDate_Hack]): OccasionEndpointTask[GetOccasionsResponse] = {
      (for {
        userId    <- decodeId(hashedUserId).fold(_ => UserId.Zero, _.fold(UserId.Zero)(UserId(_)))
        occasions <- listOccasions(userId, from.map(_.inner), to.map(_.inner))
      } yield occasions)
        .fold(
          _         => respond.NotFound(notFoundProblem(s"user $hashedUserId was not found")),
          occasions => respond.Ok(OccasionsResponseJson(occasions.map(occasion => OccasionResponseJson(occasion.startDate, occasion.isPresent)).toIndexedSeq))
        )
    }

    override def getOccasionByDate(respond: GetOccasionByDateResponse.type)(hashedUserId: String, dateString: String): OccasionEndpointTask[GetOccasionByDateResponse] = {
      (for {
        userId   <- decodeId(hashedUserId).fold(_ => UserId.Zero, _.fold(UserId.Zero)(UserId(_)))
        date     <- ZIO(LocalDate.parse(dateString))
        occasion <- getOccasion(userId, date)
      } yield occasion)
        .fold(
          _        => respond.NotFound(notFoundProblem(s"occasion with user $hashedUserId and date $dateString was not found")),
          occasion => respond.Ok(OccasionResponseJson(occasion.startDate, occasion.isPresent))
        )
    }

    override def patchOccasionByDate(respond: PatchOccasionByDateResponse.type)(hashedUserId: String, dateString: String, body: PatchOccasionRequestJson): OccasionEndpointTask[PatchOccasionByDateResponse] = {
      (for {
        userId          <- decodeId(hashedUserId).fold(_ => UserId.Zero, _.fold(UserId.Zero)(UserId(_)))
        date            <- ZIO(LocalDate.parse(dateString))
        updatedOccasion <- updateOccasion(userId, date, Occasion.Update(body.isPresent))
      } yield updatedOccasion)
        .fold(
          _               => respond.NotFound(notFoundProblem(s"occasion with user $hashedUserId and date $dateString was not found")),
          updatedOccasion => respond.Ok(OccasionResponseJson(updatedOccasion.startDate, updatedOccasion.isPresent))
        )
    }

    override def deleteOccasionByDate(respond: DeleteOccasionByDateResponse.type)(hashedUserId: String, dateString: String): OccasionEndpointTask[DeleteOccasionByDateResponse] = {
      (for {
        userId  <- decodeId(hashedUserId).fold(_ => UserId.Zero, _.fold(UserId.Zero)(UserId(_)))
        date    <- ZIO(LocalDate.parse(dateString))
        success <- deleteOccasion(userId, date)
      } yield success)
        .fold(
          _ => respond.NotFound(notFoundProblem(s"occasion with user $hashedUserId and date $dateString was not found")),
          _ => respond.NoContent
        )
    }
  }
}
