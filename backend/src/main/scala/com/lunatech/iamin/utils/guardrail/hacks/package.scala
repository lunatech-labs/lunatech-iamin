package com.lunatech.iamin.utils.guardrail

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import cats.syntax.either._
import io.circe.{Decoder, Encoder}

package object hacks {

  private val isoDateFormatter = DateTimeFormatter.ISO_DATE


  final case class FromLocalDate_Hack(inner: LocalDate)

  implicit val encodeFromLocalDate: Encoder[FromLocalDate_Hack] =
    Encoder.encodeString.contramap(_.inner.format(isoDateFormatter))
  implicit val decodeFromLocalDate: Decoder[FromLocalDate_Hack] =
    Decoder.decodeString.emap { s =>
      Either.catchNonFatal(LocalDate.parse(s)).map(FromLocalDate_Hack).leftMap(_ => "LocalDate")
    }


  final case class ToLocalDate_Hack(inner: LocalDate)

  implicit val encodeToLocalDate: Encoder[ToLocalDate_Hack] =
    Encoder.encodeString.contramap(_.inner.format(isoDateFormatter))
  implicit val decodeToLocalDate: Decoder[ToLocalDate_Hack] =
    Decoder.decodeString.emap { s =>
      Either.catchNonFatal(LocalDate.parse(s)).map(ToLocalDate_Hack).leftMap(_ => "LocalDate")
    }
}
