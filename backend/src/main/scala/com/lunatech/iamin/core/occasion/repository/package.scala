package com.lunatech.iamin.core.occasion

import java.time.LocalDate

import com.lunatech.iamin.{Occasion, UserId}
import scalaz.zio.ZIO

package object repository extends OccasionRepository.Service[OccasionRepository] {

  override def createOccasion(create: Occasion.Create): ZIO[OccasionRepository, Occasion.CreateException, Occasion] =
    ZIO.accessM(_.occasionRepository.createOccasion(create))

  override def getOccasion(userId: UserId, date: LocalDate): ZIO[OccasionRepository, Occasion.NotFound.type, Occasion] =
    ZIO.accessM(_.occasionRepository.getOccasion(userId, date))

  override def listOccasions(userId: UserId, from: Option[LocalDate], to: Option[LocalDate]): ZIO[OccasionRepository, Occasion.NotFound.type, List[Occasion]] =
    ZIO.accessM(_.occasionRepository.listOccasions(userId, from, to))

  override def updateOccasion(userId: UserId, date: LocalDate, update: Occasion.Update): ZIO[OccasionRepository, Occasion.NotFound.type, Occasion] =
    ZIO.accessM(_.occasionRepository.updateOccasion(userId, date, update))

  override def deleteOccasion(userId: UserId, date: LocalDate): ZIO[OccasionRepository, Occasion.NotFound.type, Unit] =
    ZIO.accessM(_.occasionRepository.deleteOccasion(userId, date))
}
