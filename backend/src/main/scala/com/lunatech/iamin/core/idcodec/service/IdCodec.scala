package com.lunatech.iamin.core.idcodec.service

import scalaz.zio.TaskR

trait IdCodec {

  def idCodec: IdCodec.Service[Any]
}

object IdCodec {

  trait Service[R] {

    def encodeId(id: Long): TaskR[R, String]

    def decodeId(s: String): TaskR[R, Option[Long]]
  }
}


