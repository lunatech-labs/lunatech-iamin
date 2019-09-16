package com.lunatech.iamin.components

import com.lunatech.iamin.components.Bootstrap.Button
import com.lunatech.iamin.modules.UserItem
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._

object UserList {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class UserListProps(
                            items: Seq[UserItem],
                            stateChange: UserItem => Callback,
                            editItem: UserItem => Callback,
                            deleteItem: UserItem => Callback
                          )

  private val UserList = ScalaComponent.builder[UserListProps]("UserList")
    .render_P(p => {
      val style = bss.listGroup

      def renderItem(item: UserItem) = {
        val itemStyle = style.item
        <.li(itemStyle,
          //          <.input.checkbox(^.checked := item.completed, ^.onChange --> p.stateChange(item.copy(completed = !item.completed))),
          <.span(" "),
          //          if (item.completed) <.s(item.content) else <.span(item.content),
          Button(Button.Props(p.editItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Edit"),
          Button(Button.Props(p.deleteItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Delete")
        )
      }

      <.ul(style.listGroup)(p.items toTagMod renderItem)
    })
    .build

  def apply(items: Seq[UserItem], stateChange: UserItem => Callback, editItem: UserItem => Callback, deleteItem: UserItem => Callback): Unmounted[UserListProps, Unit, Unit] =
    UserList(UserListProps(items, stateChange, editItem, deleteItem))
}
