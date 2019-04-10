package com.lunatech.iamin

import com.lunatech.iamin.config.Config
import com.lunatech.iamin.utils.DatabaseMigrator

object Main {

  def main(args: Array[String]): Unit = {

    if (args.headOption.contains("migrate")) {
      DatabaseMigrator.applyMigrations(Config.database, dryRun = false)
    }
  }
}
