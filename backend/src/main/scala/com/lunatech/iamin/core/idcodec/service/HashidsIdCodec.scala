package com.lunatech.iamin.core.idcodec.service

import com.lunatech.iamin.config.HashidsConfig
import org.hashids.Hashids
import scalaz.zio.{Task, TaskR, UIO}

trait HashidsIdCodec extends IdCodec {

  protected def hashidsConfig: HashidsConfig

  private val hashids: Hashids = new Hashids(hashidsConfig.secret.value, hashidsConfig.minLength)

  override def idCodec: IdCodec.Service[Any] = new IdCodec.Service[Any] {

    override def encodeId(id: Long): TaskR[Any, String] = UIO(hashids.encode(id))

    override def decodeId(s: String): TaskR[Any, Option[Long]] = Task.effect(hashids.decode(s).headOption)
  }
}
