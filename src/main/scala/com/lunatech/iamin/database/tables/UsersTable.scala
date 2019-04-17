// AUTO-GENERATED Slick data model for table Users, DO NOT EDIT
// generated at 2019-04-17T12:28:16.791

package com.lunatech.iamin.database.tables

import com.lunatech.iamin.database.Profile.api._
import com.lunatech.iamin.database.{Profile => profile} // Hack to satisfy the generated Users Object

import slick.model.ForeignKeyAction
/** Entity class storing rows of table Users
 *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
 *  @param displayName Database column display_name SqlType(text)
 *  @param created Database column created SqlType(timestamp) */
case class UsersRow(id: Long, displayName: String, created: java.time.LocalDateTime)
/** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, "users") {
  def * = (id, displayName, created) <> (UsersRow.tupled, UsersRow.unapply)
  /** Maps whole row to an option. Useful for outer joins. */
  def ? = ((Rep.Some(id), Rep.Some(displayName), Rep.Some(created))).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  /** Database column display_name SqlType(text) */
  val displayName: Rep[String] = column[String]("display_name")
  /** Database column created SqlType(timestamp) */
  val created: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("created")
}
/** Collection-like TableQuery object for table Users */
private object Users extends TableQuery(tag => new Users(tag))
