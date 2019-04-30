package com.lunatech.iamin.utils

object Banner {

  val banner: String =
    s"""
      | .__               .__
      | |__|____    _____ |__| ____
      | |  \\__  \\  /     \\|  |/    \\
      | |  |/ __ \\|  Y Y  \\  |   |  \\
      | |__(____  /__|_|  /__|___|  /
      |        \\/      \\/        \\/
      | Version: ${BuildInfo.version}
    """.stripMargin
}
