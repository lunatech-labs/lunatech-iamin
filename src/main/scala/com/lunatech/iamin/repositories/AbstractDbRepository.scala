package com.lunatech.iamin.repositories

import cats.effect.IO
import com.lunatech.iamin.database.Profile.api._

abstract class AbstractDbRepository(db: Database) {

  def execute[R](query: DBIO[R]): IO[R] = {
    IO.fromFuture(IO(db.run(query)))
  }
}
