package com.lunatech.iamin.domain.occasions

import java.time.LocalDate

trait OccasionRepository[F[_]] {

  def create(occasion: Occasion): F[Either[CreateFailed, Occasion]]

  def update(occasion: Occasion): F[Either[UpdateFailed, Occasion]]

  def delete(userId: Long, date: LocalDate): F[Option[Unit]]

  def get(userId: Long, date: LocalDate): F[Option[Occasion]]

  // TODO: Add pagination
  def list(userId: Long, from: LocalDate, to: LocalDate): F[Seq[Occasion]]
}
