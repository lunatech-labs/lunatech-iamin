package com.lunatech.iamin.utils

import cats.effect.{IO, Sync}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder, Response, Status}
import org.scalatest.{AppendedClues, Matchers}

trait Http4sTestSupport extends Matchers with AppendedClues {

  implicit def jsonDecoder[F[_]: Sync, A <: Product: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]
  implicit def jsonEncoder[F[_]: Sync, A <: Product: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  def check[A](
    actual: IO[Response[IO]],
    expectedStatus: Status,
    expectedBody: Option[A]
  )(
    implicit ev: EntityDecoder[IO, A]
  ): Boolean =  {
    val actualResp = actual.unsafeRunSync
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync().isEmpty
      // Verify Response's body is empty.
    )(
      expected => actualResp.as[A].unsafeRunSync == expected
    )
    statusCheck && bodyCheck
  }
}
