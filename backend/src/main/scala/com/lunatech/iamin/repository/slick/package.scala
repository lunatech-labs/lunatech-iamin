package com.lunatech.iamin.repository

import cats.effect.IO
import com.lunatech.iamin.database.Profile.api._

package object slick {

  def tx[R](query: DBIO[R])(implicit db: Database): IO[R] = {
    IO.fromFuture(IO(db.run(query)))
  }
}
