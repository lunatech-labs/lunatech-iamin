package com.lunatech.iamin.endpoints

import java.time.LocalDate

import cats.effect.Async
import cats.implicits._
import com.lunatech.iamin.domain.occasions.{CreateFailed, Occasion, OccasionRepository, UpdateFailed}
import com.lunatech.iamin.endpoints.definitions.{OccasionResponseJson, OccasionsResponseJson, PatchOccasionRequestJson, PostOccasionRequestJson}
import com.lunatech.iamin.endpoints.occasions._
import com.lunatech.iamin.utils.guardrail.hacks._

class OccasionsHandlerImpl[F[_] : Async](repo: OccasionRepository[F]) extends OccasionsHandler[F] {

  override def postOccasions(respond: PostOccasionsResponse.type)(userId: Long, body: PostOccasionRequestJson): F[PostOccasionsResponse] =
    repo.create(Occasion(userId, body.date, body.isPresent)) map {
      case Left(CreateFailed.UserNotFound) => respond.NotFound
      case Left(CreateFailed.DateAlreadyTaken) => respond.Conflict
      case Right(occasion) => respond.Ok(occasion)
    }

  override def getOccasions(respond: GetOccasionsResponse.type)(userId: Long, from: Option[FromLocalDate_Hack], to: Option[ToLocalDate_Hack]): F[GetOccasionsResponse] = {
    repo.list(
      userId = userId,
      from = from.fold(LocalDate.MIN)(_.inner),
      to = to.fold(LocalDate.MAX)(_.inner),
    ) map { occasions =>
      respond.Ok(OccasionsResponseJson(occasions.map(o => OccasionResponseJson(o.date.toString, o.isPresent)).toIndexedSeq))
    }
  }

  override def getOccasionByDate(respond: GetOccasionByDateResponse.type)(userId: Long, date: String): F[GetOccasionByDateResponse] = {
    val ldt = LocalDate.parse(date) // TODO: Purify
    repo.get(userId, ldt) map {
      case None => respond.NotFound
      case Some(occasion) => respond.Ok(occasion)
    }
  }

  override def patchOccasionByDate(respond: PatchOccasionByDateResponse.type)(userId: Long, date: String, body: PatchOccasionRequestJson): F[PatchOccasionByDateResponse] = {
    val ldt = LocalDate.parse(date) // TODO: Purify
    repo.update(Occasion(userId, ldt, body.isPresent)) map {
      case Left(UpdateFailed.UserNotFound) => respond.NotFound
      case Right(occasion) => respond.Ok(occasion)
    }
  }

  override def deleteOccasionByDate(respond: DeleteOccasionByDateResponse.type)(userId: Long, date: String): F[DeleteOccasionByDateResponse] = {
    val ldt = LocalDate.parse(date) // TODO: Purify
    repo.delete(userId, ldt) map {
      case None => respond.NotFound
      case Some(_) => respond.NoContent
    }
  }

  private implicit def occasionIntoOccasionResponseJson(occasion: Occasion): OccasionResponseJson =
    OccasionResponseJson(occasion.date.toString, occasion.isPresent)
}
