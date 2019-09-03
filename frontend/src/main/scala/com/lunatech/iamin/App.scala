import scala.scalajs.js
import org.scalajs.dom
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

case class AppState(user: String)

object IaminApp {

  val component = ScalaComponent.builder[Unit]("Iamin")
    .initialState(AppState("toto"))
    .renderBackend[AppBackend]
    .build

  class AppBackend($: BackendScope[Unit, AppState]) {

    def render(s: AppState) = <.div(<.p(s.user))

  }
}

object App extends js.JSApp {

  override def main(): Unit = {
    IaminApp.component().renderIntoDOM(dom.document.getElementById("mount"))
  }
}
