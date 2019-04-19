package com.lunatech.iamin.utils

import com.lunatech.iamin.database.Profile.api.Database
import com.opentable.db.postgres.embedded.EmbeddedPostgres

trait DatabaseTest {
  import DatabaseTest._

  lazy val testDatabase: Database = db
}

object DatabaseTest {
  lazy val db: Database = {

    val embeddedPostgres = EmbeddedPostgres.start()
    val ds = embeddedPostgres.getPostgresDatabase
    val db = Database.forDataSource(ds, None)

    com.lunatech.iamin.database.Database.migrate(ds.getConnection).unsafeRunSync

    db
  }
}
