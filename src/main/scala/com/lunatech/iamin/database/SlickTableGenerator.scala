package com.lunatech.iamin.database

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import javax.sql.DataSource
import slick.codegen.SourceCodeGenerator
import slick.jdbc.meta.MTable
import slick.model.Model
import slick.sql.SqlProfile.ColumnOption

import scala.concurrent.ExecutionContext

object SlickTableGenerator extends IOApp {

  private val ExcludedTables = Seq(
    "databasechangeloglock", // Liquibase specific
    "databasechangelog" // Liquibase specific
  )

  override def run(args: List[String]): IO[ExitCode] =
    Resource.make(IO(EmbeddedPostgres.start())) { db =>
      IO(db.close())
    }.use { database =>
      for {
        outputDir <- parseOutputDir(args)
        dataSource <- IO.pure(database.getPostgresDatabase)
        _ <- Database.migrate(dataSource.getConnection)
        model <- createDatabaseModel(dataSource, ExecutionContext.global)
        generator <- IO.pure(new CustomSourceCodeGenerator(model))
        _ <- writeFiles(generator, outputDir)
        exitCode <- IO.pure(ExitCode.Success)
      } yield exitCode
    }

  private def parseOutputDir(args: List[String]): IO[String] =
    args.headOption.fold(IO.raiseError[String](new IllegalArgumentException("missing output directory")))(IO.pure)

  private def createDatabaseModel(dataSource: DataSource, ec: ExecutionContext): IO[Model] =
    for {
      db <- IO(com.lunatech.iamin.database.Profile.api.Database.forDataSource(dataSource, None))
      tablesAndViews <- IO(MTable.getTables(None, None, None, Some(Seq("TABLE", "VIEW"))).map(ts => ts.filterNot(t => ExcludedTables.contains(t.name.name)))(ec))
      action <- IO(Profile.createModel(Some(tablesAndViews))(ec))
      model <- IO.fromFuture(IO(db.run(action)))
    } yield model

  private def writeFiles(generator: SourceCodeGenerator, sourceDir: String): IO[Unit] = IO {
    val profileClass = Profile.getClass

    generator.writeToMultipleFiles(
      profileClass.getCanonicalName.replace("$", ""),
      sourceDir,
      profileClass.getPackage.getName + ".tables"
    )
  }

  class CustomSourceCodeGenerator(model: Model) extends SourceCodeGenerator(model) {
    override val ddlEnabled: Boolean = false

    override def entityName: String => String = _.toLowerCase.toCamelCase + "Row"

    override def tableName: String => String = _.toLowerCase.toCamelCase

    override def Table = new Table(_) {
      override def PlainSqlMapper = new PlainSqlMapperDef {
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
