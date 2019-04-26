package com.lunatech.iamin.domain.users

import cats.Functor
import cats.data.OptionT

class UserService[F[_] : Functor](repo: UserRepository[F]) {

  def list(offset: Long, limit: Int): F[Seq[User]] =
    repo.list(offset, limit)

  def get(id: Long): OptionT[F, User] =
    OptionT(repo.get(id))

  def create(displayName: String): F[User] =
    repo.create(User(0, displayName))

  def update(id: Long, newDisplayName: String): OptionT[F, User] =
    OptionT(repo.update(User(id, newDisplayName)))

  def delete(id: Long): OptionT[F, Unit] =
    OptionT(repo.delete(id))
}
