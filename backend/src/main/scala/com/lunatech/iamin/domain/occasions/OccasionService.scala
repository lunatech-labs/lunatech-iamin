package com.lunatech.iamin.domain.occasions

import java.time.LocalDate

import cats.data.{EitherT, OptionT}

class OccasionService[F[_]](repo: OccasionRepository[F]) {

  def create(id: Long, date: LocalDate, isPresent: Boolean): EitherT[F, CreateFailed, Occasion] =
    EitherT(repo.create(Occasion(id, date, isPresent)))

  def list(id: Long, from: LocalDate, to: LocalDate): F[Seq[Occasion]] =
    repo.list(id, from, to)

  def get(id: Long, date: LocalDate): OptionT[F, Occasion] =
    OptionT(repo.get(id, date))

  def update(id: Long, date: LocalDate, newIsPresent: Boolean): EitherT[F, UpdateFailed, Occasion] =
    EitherT(repo.update(Occasion(id, date, newIsPresent)))

  def delete(id: Long, date: LocalDate): OptionT[F, Unit] =
    OptionT(repo.delete(id, date))
}
