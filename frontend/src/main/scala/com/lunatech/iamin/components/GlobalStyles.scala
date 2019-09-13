package com.lunatech.iamin.components

import com.lunatech.iamin.CssSettings._

object GlobalStyles extends StyleSheet.Inline {
  import dsl._

  style(unsafeRoot("body")(
    paddingTop(70.px))
  )

  val bootstrapStyles = new BootstrapStyles
}
