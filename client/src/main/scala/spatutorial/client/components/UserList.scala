package spatutorial.client.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scalacss.ScalaCssReact._
import spatutorial.client.components.Bootstrap.Button
import spatutorial.shared._

object UserList {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class UserListProps(
                            items: Seq[User],
                            stateChange: User => Callback,
                            editItem: User => Callback,
                            deleteItem: User => Callback
                          )

  private val UserList = ScalaComponent.builder[UserListProps]("UserList")
    .render_P(p => {
      val style = bss.listGroup
      def renderItem(item: User) = {
        val itemStyle = style.item
        <.li(itemStyle,
          <.span(item.name),
          Button(Button.Props(p.editItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Edit"),
          Button(Button.Props(p.deleteItem(item), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Delete")
        )
      }
      <.ul(style.listGroup)(p.items toTagMod renderItem)
    })
    .build

  def apply(items: Seq[User], stateChange: User => Callback, editItem: User => Callback, deleteItem: User => Callback) = {
    UserList(UserListProps(items, stateChange, editItem, deleteItem))
  }

}