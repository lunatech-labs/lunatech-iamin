package com.lunatech.iamin.utils

import com.lunatech.iamin.config.HashidsConfig
import org.hashids.Hashids

trait IdObfuscator {

  def obfuscate(input: Long): String

  def deobfuscate(input: String): Long
}

class HashidsIdObfuscator(config: HashidsConfig) extends IdObfuscator {

  val hashids: Hashids = new Hashids(config.secret, config.minLength)

  override def obfuscate(input: Long): String = {
    hashids.encode(input)
  }

  override def deobfuscate(input: String): Long = {
    hashids.decode(input).headOption getOrElse -1
  }
}
