// AUTO-GENERATED Slick data model for table Occasions, DO NOT EDIT
// generated at 2019-04-24T13:45:59.786

package com.lunatech.iamin.database.tables

import com.lunatech.iamin.database.Profile.api._
import com.lunatech.iamin.database.{Profile => profile} // Hack to satisfy the generated Occasions Object

import slick.model.ForeignKeyAction
/** Entity class storing rows of table Occasions
 *  @param userId Database column user_id SqlType(int8)
 *  @param date Database column date SqlType(date)
 *  @param isPresent Database column is_present SqlType(bool), Default(true) */
case class OccasionsRow(userId: Long, date: java.time.LocalDate, isPresent: Boolean = true)
/** Table description of table occasions. Objects of this class serve as prototypes for rows in queries. */
class Occasions(_tableTag: Tag) extends profile.api.Table[OccasionsRow](_tableTag, "occasions") {
  def * = (userId, date, isPresent) <> (OccasionsRow.tupled, OccasionsRow.unapply)
  /** Maps whole row to an option. Useful for outer joins. */
  def ? = ((Rep.Some(userId), Rep.Some(date), Rep.Some(isPresent))).shaped.<>({r=>import r._; _1.map(_=> OccasionsRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  /** Database column user_id SqlType(int8) */
  val userId: Rep[Long] = column[Long]("user_id")
  /** Database column date SqlType(date) */
  val date: Rep[java.time.LocalDate] = column[java.time.LocalDate]("date")
  /** Database column is_present SqlType(bool), Default(true) */
  val isPresent: Rep[Boolean] = column[Boolean]("is_present", O.Default(true))

  /** Primary key of Occasions (database name occasions_pkey) */
  val pk = primaryKey("occasions_pkey", (userId, date))

  /** Foreign key referencing Users (database name FK_availabilities_id_users_id) */
  lazy val usersFk = foreignKey("FK_availabilities_id_users_id", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
}
/** Collection-like TableQuery object for table Occasions */
private object Occasions extends TableQuery(tag => new Occasions(tag))
