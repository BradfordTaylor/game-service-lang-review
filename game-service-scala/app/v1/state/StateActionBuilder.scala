package v1.state

import javax.inject.Inject

import net.logstash.logback.marker.LogstashMarker
import play.api.{Logger, MarkerContext}
import play.api.http.{FileMimeTypes, HttpVerbs}
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait StateRequestHeader extends MessagesRequestHeader with PreferredMessagesProvider
class StateRequest[A](request: Request[A], val messagesApi: MessagesApi) extends WrappedRequest(request) with StateRequestHeader

trait RequestMarkerContext {
  import net.logstash.logback.marker.Markers

  private def marker(tuple: (String, Any)) = Markers.append(tuple._1, tuple._2)

  private implicit class RichLogstashMarker(marker1: LogstashMarker) {
    def &&(marker2: LogstashMarker): LogstashMarker = marker1.and(marker2)
  }

  implicit def requestHeaderToMarkerContext(implicit request: RequestHeader): MarkerContext = {
    MarkerContext {
      marker("id" -> request.id) && marker("host" -> request.host) && marker("remoteAddress" -> request.remoteAddress)
    }
  }
}

class StateActionBuilder @Inject()(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)
  (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[StateRequest, AnyContent]
  with RequestMarkerContext
  with HttpVerbs {

  val parser: BodyParser[AnyContent] = playBodyParsers.anyContent

  type StateRequestBlock[A] = StateRequest[A] => Future[Result]

  private val logger = Logger(this.getClass)

  override def invokeBlock[A](request: Request[A],
                              block: StateRequestBlock[A]): Future[Result] = {
    implicit val markerContext: MarkerContext = requestHeaderToMarkerContext(request)
    logger.trace(s"invokeBlock: ")
    val future = block(new StateRequest(request, messagesApi))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }
}

case class StateControllerComponents @Inject()(stateActionBuilder: StateActionBuilder,
                                               stateResourceHandler: StateResourceHandler,
                                               actionBuilder: DefaultActionBuilder,
                                               parsers: PlayBodyParsers,
                                               messagesApi: MessagesApi,
                                               langs: Langs,
                                               fileMimeTypes: FileMimeTypes,
                                               executionContext: scala.concurrent.ExecutionContext)
  extends ControllerComponents

class StateBaseController @Inject()(scc: StateControllerComponents) extends BaseController with RequestMarkerContext {
  override protected def controllerComponents: ControllerComponents = scc

  def StateAction: StateActionBuilder = scc.stateActionBuilder

  def stateResourceHandler: StateResourceHandler = scc.stateResourceHandler
}
