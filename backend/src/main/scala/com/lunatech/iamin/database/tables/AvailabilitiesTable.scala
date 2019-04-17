// AUTO-GENERATED Slick data model for table Availabilities, DO NOT EDIT
// generated at 2019-04-17T12:28:16.791

package com.lunatech.iamin.database.tables

import com.lunatech.iamin.database.Profile.api._
import com.lunatech.iamin.database.{Profile => profile} // Hack to satisfy the generated Availabilities Object

import slick.model.ForeignKeyAction
/** Entity class storing rows of table Availabilities
 *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
 *  @param userId Database column user_id SqlType(int8)
 *  @param isPresent Database column is_present SqlType(bool), Default(true)
 *  @param date Database column date SqlType(date) */
case class AvailabilitiesRow(id: Long, userId: Long, isPresent: Boolean = true, date: java.sql.Date)
/** Table description of table availabilities. Objects of this class serve as prototypes for rows in queries. */
class Availabilities(_tableTag: Tag) extends profile.api.Table[AvailabilitiesRow](_tableTag, "availabilities") {
  def * = (id, userId, isPresent, date) <> (AvailabilitiesRow.tupled, AvailabilitiesRow.unapply)
  /** Maps whole row to an option. Useful for outer joins. */
  def ? = ((Rep.Some(id), Rep.Some(userId), Rep.Some(isPresent), Rep.Some(date))).shaped.<>({r=>import r._; _1.map(_=> AvailabilitiesRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

  /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
  val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  /** Database column user_id SqlType(int8) */
  val userId: Rep[Long] = column[Long]("user_id")
  /** Database column is_present SqlType(bool), Default(true) */
  val isPresent: Rep[Boolean] = column[Boolean]("is_present", O.Default(true))
  /** Database column date SqlType(date) */
  val date: Rep[java.sql.Date] = column[java.sql.Date]("date")

  /** Foreign key referencing Users (database name FK_availabilities_id_users_id) */
  lazy val usersFk = foreignKey("FK_availabilities_id_users_id", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)

  /** Uniqueness Index over (userId,date) (database name availabilities_user_id_date_key) */
  val index1 = index("availabilities_user_id_date_key", (userId, date), unique=true)
}
/** Collection-like TableQuery object for table Availabilities */
private object Availabilities extends TableQuery(tag => new Availabilities(tag))
