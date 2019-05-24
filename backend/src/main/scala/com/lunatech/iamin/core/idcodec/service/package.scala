package com.lunatech.iamin.core.idcodec

import scalaz.zio.{TaskR, ZIO}

package object service extends IdCodec.Service[IdCodec] {

  override def encodeId(id: Long): TaskR[IdCodec, String] =
    ZIO.accessM(_.idCodec.encodeId(id))

  override def decodeId(s: String): TaskR[IdCodec, Option[Long]] =
    ZIO.accessM(_.idCodec.decodeId(s))
}
