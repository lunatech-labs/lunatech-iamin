package com.lunatech.iamin.domain.users

trait UserRepositoryAlgebra[F[_]] {

  /**
    * Creates a new user, the `id` of the user might be replaced with something else.
    * @param user User to create
    * @return Created user
    */
  def create(user: User): F[User]

  /**
    * Updates an existing user, correlated by `id`.
    * @param user User to update
    * @return Updated user or `None` if the user was not found
    */
  def update(user: User): F[Option[User]]

  /**
    * Deletes an existing user, specified by its `id`.
    * @param id Id of the user to delete
    * @return Unit or `None` if the user was not found
    */
  def delete(id: Long): F[Option[Unit]]

  /**
    * Retrieves a user, specified by its `id`.
    * @param id Id of the user to retrieve
    * @return User or `None` if the user was not found
    */
  def get(id: Long): F[Option[User]]

  /**
    * Retrieves a list of user.
    * @param offset Id of the first user to return in the list
    * @param limit Number of users to return
    * @return List of users
    */
  def list(offset: Long, limit: Int): F[Seq[User]]
}
