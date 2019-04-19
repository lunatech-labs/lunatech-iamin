package com.lunatech.iamin.utils

@SuppressWarnings(Array("org.wartremover.warts.FinalCaseClass"))
case class BuildInfo(
  name: String = _BuildInfo.name,
  version: String = _BuildInfo.version,
  sbtVersion: String = _BuildInfo.sbtVersion,
  scalaVersion: String = _BuildInfo.scalaVersion
) {
  override def toString: String = _BuildInfo.toString
}

object BuildInfo extends BuildInfo(
  name = _BuildInfo.name,
  version = _BuildInfo.version,
  sbtVersion = _BuildInfo.sbtVersion,
  scalaVersion = _BuildInfo.scalaVersion
)
