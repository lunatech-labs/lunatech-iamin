package com.lunatech.iamin.endpoints

import java.time.LocalDate

import cats.effect.Async
import cats.implicits._
import com.lunatech.iamin.domain.occasions.{CreateFailed, Occasion, OccasionService, UpdateFailed}
import com.lunatech.iamin.endpoints.definitions.{OccasionResponseJson, OccasionsResponseJson, PatchOccasionRequestJson, PostOccasionRequestJson}
import com.lunatech.iamin.endpoints.occasions._
import com.lunatech.iamin.utils.guardrail.hacks._

class OccasionsHandlerImpl[F[_] : Async](service: OccasionService[F]) extends OccasionsHandler[F] {

  override def postOccasions(respond: PostOccasionsResponse.type)(userId: Long, body: PostOccasionRequestJson): F[PostOccasionsResponse] = {
    service.create(userId, body.date, body.isPresent).fold(
      {
        case CreateFailed.UserNotFound => respond.NotFound
        case CreateFailed.DateAlreadyTaken => respond.Conflict
      },
      respond.Ok(_)
    )
  }

  override def getOccasions(respond: GetOccasionsResponse.type)(userId: Long, from: Option[FromLocalDate_Hack], to: Option[ToLocalDate_Hack]): F[GetOccasionsResponse] = {
    service.list(
      userId,
      from.fold(LocalDate.MIN)(_.inner),
      to.fold(LocalDate.MAX)(_.inner),
    ) map { occasion =>
      respond.Ok(OccasionsResponseJson(occasion.map(o => OccasionResponseJson(o.date.toString, o.isPresent)).toIndexedSeq))
    }
  }

  override def getOccasionByDate(respond: GetOccasionByDateResponse.type)(userId: Long, date: String): F[GetOccasionByDateResponse] = {
    val ldt = LocalDate.parse(date) // TODO: Purify
    service.get(userId, ldt).fold[GetOccasionByDateResponse](respond.NotFound)(respond.Ok(_))
  }

  override def patchOccasionByDate(respond: PatchOccasionByDateResponse.type)(userId: Long, date: String, body: PatchOccasionRequestJson): F[PatchOccasionByDateResponse] = {
    val ldt = LocalDate.parse(date) // TODO: Purify
    service.update(userId, ldt, body.isPresent).fold(
      {
        case UpdateFailed.UserNotFound => respond.NotFound
      },
      respond.Ok(_)
    )
  }

  override def deleteOccasionByDate(respond: DeleteOccasionByDateResponse.type)(userId: Long, date: String): F[DeleteOccasionByDateResponse] = {
    val ldt = LocalDate.parse(date) // TODO: Purify
    service.delete(userId, ldt).fold[DeleteOccasionByDateResponse](respond.NotFound)(_ => respond.NoContent)
  }

  private implicit def occasionIntoOccasionResponseJson(occasion: Occasion): OccasionResponseJson =
    OccasionResponseJson(occasion.date.toString, occasion.isPresent)
}
