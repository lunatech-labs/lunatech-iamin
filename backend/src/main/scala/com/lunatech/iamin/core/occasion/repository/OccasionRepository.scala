package com.lunatech.iamin.core.occasion.repository

import java.time.LocalDate

import com.lunatech.iamin.{Occasion, UserId}
import scalaz.zio.ZIO

trait OccasionRepository {

  def occasionRepository: OccasionRepository.Service[Any]
}

object OccasionRepository {

  trait Service[R] {

    def createOccasion(create: Occasion.Create): ZIO[R, Occasion.CreateException, Occasion]

    def getOccasion(userId: UserId, date: LocalDate): ZIO[R, Occasion.NotFound.type, Occasion]

    def listOccasions(userId: UserId, from: Option[LocalDate], to: Option[LocalDate]): ZIO[R, Occasion.NotFound.type, List[Occasion]]

    def updateOccasion(userId: UserId, date: LocalDate, update: Occasion.Update): ZIO[R, Occasion.NotFound.type, Occasion]

    def deleteOccasion(userId: UserId, date: LocalDate): ZIO[R, Occasion.NotFound.type, Unit]
  }
}
