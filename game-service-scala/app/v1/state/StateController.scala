package v1.state

import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{mapping, number}
import play.api.libs.json.JsValue
//import play.api.data.validation.Constraints._
import play.api.data.Forms.{nonEmptyText, optional, boolean, text}
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}

case class StateForm(gameGuid: String, status: String, currentGameType: String, currentGameState: JsValue)


class StateController @Inject()(cc: StateControllerComponents)(implicit ec: ExecutionContext)
  extends StateBaseController(cc) {

  private val logger = Logger(getClass)

  implicit val stateFormReads = Json.reads[StateForm]

  /**
    * The mapping for the State form.
    */
//  private val stateForm: Form[StateForm] = {
//    import play.api.data.Forms._
//
//    Form (
//      mapping(
//        "game_guid" -> text, // optional(available_game_types),
//        "status" -> text,
//        "current_game_type" -> nonEmptyText,
//        "current_game_state" -> nonEmptyText
//      )(StateForm.apply)(StateForm.unapply))
//  }

  /**
    * The index action.
    */
  def index: Action[AnyContent] = StateAction.async { implicit request =>
    logger.trace("index: ")
    stateResourceHandler.find.map { states =>
      Ok(Json.toJson(states))
    }
  }

  def process(id: String): Action[AnyContent] = StateAction.async { implicit request =>
    logger.trace(s"process: id = $id")
    logger.trace(s"request: request = $request")
    val stateFormFromJson: JsResult[StateForm] = Json.fromJson[StateForm](request.body.asJson.get)


    def failure(badForm: StateForm) = {
      Future.successful(BadRequest(badForm))
    }

    def success(input: StateForm) = {
      stateResourceHandler.update(id.toInt, input).map { state =>
        Accepted(Json.toJson(state))
      }
    }

    stateFormFromJson.fold(failure,success)
//    processJsonStateCreate(id)
  }

  def show(id: String): Action[AnyContent] = StateAction.async { implicit request =>
    logger.trace(s"show: id = $id")
    stateResourceHandler.lookup(id).map { state =>
      Ok(Json.toJson(state))
    }
  }

  def update(id: String): Action[AnyContent] = StateAction.async { implicit request =>
    logger.trace(s"update: id = $id")
    processJsonStateUpdate(id)
  }
  
  def delete(id: String): Action[AnyContent] = StateAction.async { implicit request =>
    logger.trace(s"delete: id = $id")
    stateResourceHandler.delete(id).map { count =>
      NoContent
    }

  }

  private def processJsonStateCreate[A](id: String)(implicit request: StateRequest[A]): Future[Result] = {
    def failure(badForm: Form[StateForm]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: StateForm) = {
      stateResourceHandler.create(id.toInt, input).map { state =>
        Created(Json.toJson(state))
      }
    }

    stateForm.bindFromRequest().fold(failure, success)


  }

  private def processJsonStateUpdate[A](id: String)(implicit request: StateRequest[A]): Future[Result] = {
    def failure(badForm: Form[StateForm]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: StateForm) = {
      stateResourceHandler.update(id.toInt, input).map { state =>
        Accepted(Json.toJson(state))
      }
    }

    stateForm.bindFromRequest().fold(failure, success)

  }
}
