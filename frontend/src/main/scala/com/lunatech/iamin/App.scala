import scala.scalajs.js
import org.scalajs.dom
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import com.lunatech.iamin.IaminApp

object App extends js.JSApp {

  override def main(): Unit = {
    IaminApp.component().renderIntoDOM(dom.document.getElementById("mount"))
  }
}
