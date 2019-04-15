package com.lunatech.iamin.database

import cats.effect.{ExitCode, IO, IOApp}
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import doobie.util.transactor.Transactor
import javax.sql.DataSource
import slick.codegen.SourceCodeGenerator
import slick.jdbc.meta.MTable
import slick.model.Model
import slick.sql.SqlProfile.ColumnOption

import scala.concurrent.ExecutionContext

object SlickTableGenerator extends IOApp {

  private val ExcludedTables = Seq(
    "databasechangeloglock", "databasechangelog" // Liquibase specific tables
  )

  override def run(args: List[String]): IO[ExitCode] =
    for {
      outDir    <- args.headOption.fold(IO.raiseError[String](new RuntimeException("param outDir is missing")))(IO.pure)
      db        <- IO(EmbeddedPostgres.start())
      xa        <- IO(Transactor.fromConnection[IO](db.getPostgresDatabase.getConnection, ExecutionContext.global))
      _         <- xa.configure(Database.init)
      model     <- createDatabaseModel(db.getPostgresDatabase, ExecutionContext.global)
      generator <- IO.pure(new CustomSourceCodeGenerator(model))
      _         <- writeFiles(generator, outDir)
      exitCode  <- IO.pure(ExitCode.Success)
    } yield exitCode

  private def createDatabaseModel(dataSource: DataSource, ec: ExecutionContext): IO[Model] =
    for {
      db             <- IO(com.lunatech.iamin.database.IaminPostgresProfile.api.Database.forDataSource(dataSource, None))
      tablesAndViews <- IO(MTable.getTables(None, None, None, Some(Seq("TABLE", "VIEW"))).map(ts => ts.filterNot(t => ExcludedTables.contains(t.name.name)))(ec))
      action         <- IO(IaminPostgresProfile.createModel(Some(tablesAndViews))(ec))
      model          <- IO.fromFuture(IO(db.run(action)))
    } yield model

  private def writeFiles(generator: SourceCodeGenerator, sourceDir: String): IO[Unit] = IO {
    val dbPackage = "com.lunatech.iamin.database"

    generator.writeToMultipleFiles(
      dbPackage + ".IaminPostgresProfile",
      sourceDir,
      dbPackage + ".tables"
    )
  }

  class CustomSourceCodeGenerator(model: Model) extends SourceCodeGenerator(model) {
    override val ddlEnabled: Boolean = false

    override def entityName: String => String = _.toLowerCase.toCamelCase + "Row"

    override def tableName: String => String = _.toLowerCase.toCamelCase

    override def Table = new Table(_) {
      override def PlainSqlMapper= new PlainSqlMapperDef {
        override def enabled: Boolean = false

        override def code: String = super.code
      }

      override def Column = new Column(_) {
        override def rawType: String = {
          model.options.find(_.isInstanceOf[ColumnOption.SqlType]).flatMap {
            tpe =>
              tpe.asInstanceOf[ColumnOption.SqlType].typeName match {
                case "_text" | "text[]" | "_varchar" | "varchar[]" => Option("List[String]")
                case "_int8" | "int8[]" => Option("List[Long]")
                case "_int4" | "int4[]" => Option("List[Int]")
                case "_int2" | "int2[]" => Option("List[Short]")
                case _ => None
              }
          }.getOrElse {
            model.tpe match {
              case "java.sql.Timestamp" => "java.time.LocalDateTime"
              case _ => super.rawType
            }
          }
        }
      }
    }
  }
}
