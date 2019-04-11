package com.lunatech.iamin.database

import com.github.tminglei.slickpg.str.PgStringSupport
import com.github.tminglei.slickpg.{ExPostgresProfile, PgArraySupport}
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities

trait IaminPostgresProfile
  extends ExPostgresProfile
  with PgArraySupport
  with PgStringSupport {

  protected override def computeCapabilities: Set[Capability] =
    super.computeCapabilities +
      JdbcCapabilities.insertOrUpdate // Postgres native upsert

  override val api: API = IaminPostgresApi

  object IaminPostgresApi
    extends API
    with ArrayImplicits
    with PgStringImplicits {

    implicit val listOfStringTypeMapper: DriverJdbcType[Seq[String]] =
      new SimpleArrayJdbcType[String]("text").to(_.seq)
  }
}

object IaminPostgresProfile extends IaminPostgresProfile