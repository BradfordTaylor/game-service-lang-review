package v1.state

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class StateRouter @Inject()(controller: StateController) extends SimpleRouter{
  val prefix = "/api/v1/arcade/states/room"

  def link(bay: Int): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / bay.toString
    url.toString()
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/$id") =>
      controller.process(id)

    case PUT(p"/$id") =>
      controller.update(id)

    case GET(p"/$id") =>
      controller.show(id)

    case DELETE(p"/$id") =>
      controller.delete(id)
  }

}
