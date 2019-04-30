package com.lunatech.iamin.endpoints

import java.time.LocalDate

import cats.effect.Async
import cats.implicits._
import com.lunatech.iamin.domain.occasions.{CreateFailed, Occasion, OccasionService, UpdateFailed}
import com.lunatech.iamin.endpoints.definitions.{OccasionResponseJson, OccasionsResponseJson, PatchOccasionRequestJson, PostOccasionRequestJson}
import com.lunatech.iamin.endpoints.occasions._
import com.lunatech.iamin.utils.IdObfuscator
import com.lunatech.iamin.utils.guardrail.hacks._
import org.http4s.Status

class OccasionsHandlerImpl[F[_] : Async](service: OccasionService[F], obs: IdObfuscator) extends OccasionsHandler[F] {

  override def postOccasions(respond: PostOccasionsResponse.type)
                            (obfuscatedUserId: String, body: PostOccasionRequestJson): F[PostOccasionsResponse] = {
    val userId = obs deobfuscate obfuscatedUserId

    service.create(userId, body.date, body.isPresent).fold(
      {
        case CreateFailed.UserNotFound =>
          respond.NotFound(problemOf(Status.NotFound, s"User '$obfuscatedUserId' was not found"))
        case CreateFailed.DateAlreadyTaken =>
          respond.Conflict(
            problemOf(Status.Conflict, s"Occasion with date '${body.date}' was not already taken")
          )
      },
      respond.Ok(_)
    )
  }

  override def getOccasions(respond: GetOccasionsResponse.type)
                           (obfuscatedUserId: String, from: Option[FromLocalDate_Hack], to: Option[ToLocalDate_Hack])
  : F[GetOccasionsResponse] = {
    val userId = obs deobfuscate obfuscatedUserId
    service.list(
      userId,
      from.fold(LocalDate.MIN)(_.inner),
      to.fold(LocalDate.MAX)(_.inner)
    ) map { occasion =>
      respond.Ok(OccasionsResponseJson(occasion.map(o => OccasionResponseJson(o.date, o.isPresent)).toIndexedSeq))
    }
  }

  override def getOccasionByDate(respond: GetOccasionByDateResponse.type)
                                (obfuscatedUserId: String, date: String): F[GetOccasionByDateResponse] = {
    val userId = obs deobfuscate obfuscatedUserId
    val ldt = LocalDate.parse(date) // TODO: Purify

    service.get(userId, ldt).fold[GetOccasionByDateResponse](
      respond.NotFound(problemOf(Status.NotFound, s"User '$obfuscatedUserId' was not found"))
    )(
      respond.Ok(_)
    )
  }

  override def patchOccasionByDate(respond: PatchOccasionByDateResponse.type)
                                  (obfuscatedUserId: String, date: String, body: PatchOccasionRequestJson)
  : F[PatchOccasionByDateResponse] = {
    val userId = obs deobfuscate obfuscatedUserId
    val ldt = LocalDate.parse(date) // TODO: Purify

    service.update(userId, ldt, body.isPresent).fold(
      {
        case UpdateFailed.UserNotFound =>
          respond.NotFound(problemOf(Status.NotFound, s"User '$obfuscatedUserId' was not found"))
      },
      respond.Ok(_)
    )
  }

  override def deleteOccasionByDate(respond: DeleteOccasionByDateResponse.type)
                                   (obfuscatedUserId: String, date: String): F[DeleteOccasionByDateResponse] = {
    val userId = obs deobfuscate obfuscatedUserId
    val ldt = LocalDate.parse(date) // TODO: Purify

    service.delete(userId, ldt).fold[DeleteOccasionByDateResponse](
      respond.NotFound(problemOf(Status.NotFound, s"User '$obfuscatedUserId' was not found"))
    )(
      _ => respond.NoContent
    )
  }

  private implicit def occasionIntoOccasionResponseJson(occasion: Occasion): OccasionResponseJson =
    OccasionResponseJson(occasion.date, occasion.isPresent)
}
