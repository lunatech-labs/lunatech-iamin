package com.lunatech.iamin.helpers

import cats.effect.{ContextShift, IO}
import com.lunatech.iamin.config.Config
import doobie.Transactor
import org.scalatest.EitherValues

trait DoobieTest extends EitherValues {

  implicit private val cs: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)

  @SuppressWarnings(Array("org.wartremover.warts.EitherProjectionPartial"))
  lazy private val dbConfig = Config.load.right.value.database

  val transactor: Transactor[IO] = Transactor.fromDriverManager[IO](
    dbConfig.driver,
    dbConfig.url,
    dbConfig.user,
    dbConfig.password.value
  )
}
