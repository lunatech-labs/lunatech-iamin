package com.lunatech.iamin.utils

import cats.effect.IO
import com.lunatech.iamin.database.Profile.api._

abstract class AbstractDatabaseRepository(db: Database) {

  def execute[R](query: DBIO[R]): IO[R] = {
    IO.fromFuture(IO(db.run(query)))
  }
}
