package com.lunatech.iamin.repository

import java.time.LocalDateTime

import cats.Applicative
import cats.effect.LiftIO
import cats.implicits._
import com.lunatech.iamin.database.Profile.api._
import com.lunatech.iamin.database.tables.{Tables, UsersRow}
import com.lunatech.iamin.domain.users.{User, UserRepository}

class SlickUserRepository[F[_] : Applicative : LiftIO](db: Database) extends UserRepository[F] {

  private implicit val database: Database = db

  /** @inheritdoc*/
  override def create(user: User): F[User] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Users
        .returning(Tables.Users.map(_.id))
        .into((row, id) => row.copy(id = id)) += UsersRow(0, user.displayName, LocalDateTime.now)
    } map (r => User(r.id, r.displayName))
  }

  /** @inheritdoc*/
  override def update(user: User): F[Option[User]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Users
        .filter(_.id === user.id.bind)
        .map(_.displayName)
        .update(user.displayName)
    } map { affectedRows =>
      if (affectedRows eqv 1) user.some
      else none[User]
    }
  }

  /** @inheritdoc*/
  override def delete(id: Long): F[Option[Unit]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Users
        .filter(_.id === id.bind)
        .delete
    } map { affectedRows =>
      if (affectedRows eqv 1) ().some
      else none[Unit]
    }
  }

  /** @inheritdoc*/
  override def get(id: Long): F[Option[User]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Users
        .filter(_.id === id.bind)
        .result
        .headOption
    } map { rows => rows.map(r => User(r.id, r.displayName)) }
  }

  /** @inheritdoc*/
  override def list(offset: Long, limit: Int): F[Seq[User]] = implicitly[LiftIO[F]].liftIO {
    tx {
      Tables.Users
        .filter(_.id > offset.bind)
        .take(limit)
        .sortBy(_.id)
        .result
    } map { rows => rows.map(r => User(r.id, r.displayName)) }
  }
}
