package v1.state

import javax.inject.{Inject, Provider}

import play.api.MarkerContext
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

case class StateResource(id: String, bay: Int, game_guid: String, status: String, current_game_type: String, current_game_state: JsValue)

object StateResource {
  implicit val implicitWrites = new Writes[StateResource] {
    def writes(state: StateResource): JsValue = {
      Json.obj(
        "bay" -> state.bay,
        "game_guid" -> state.game_guid,
        "status" -> state.status,
        "current_game_type" -> state.current_game_type,
        "current_game_state" -> state.current_game_state
      )
    }
  }
}



class StateResourceHandler @Inject()(routerProvider: Provider[StateRouter],
                                   stateRepository: StateRepositoryImpl)(implicit ec: ExecutionContext){

  def create(id: Int, stateInput: StateForm)(implicit mc: MarkerContext): Future[StateResource] = {
    val data = StateData(0L, id, stateInput.gameGuid, stateInput.status, stateInput.currentGameType, Json.toJson(stateInput.currentGameState))

    stateRepository.create(data).map { id =>
      createStateResource(data)
    }
  }

  def update(id: Int, stateInput: StateForm)(implicit mc: MarkerContext): Future[StateResource] = {
    val data = StateData(0L, id, stateInput.gameGuid, stateInput.status, stateInput.currentGameType, Json.toJson(stateInput.currentGameState))
    stateRepository.update(data).map { id =>
      createStateResource(data)
    }
  }

  def lookup(id: String) (implicit mc: MarkerContext): Future[StateResource] = {
    val stateFuture = stateRepository.get(id.toInt)
    stateFuture.map {  stateData =>
      createStateResource(stateData)
    }
  }

  def find(implicit mc: MarkerContext): Future[Iterable[StateResource]] = {
    stateRepository.list().map { stateDataList =>
      stateDataList.map(stateData => createStateResource(stateData))
    }
  }

  def delete(id: String) (implicit mc: MarkerContext): Future[Int] = {
    stateRepository.delete(id.toInt)
  }

  private def createStateResource(p: StateData): StateResource = {
    StateResource(p.id.toString, p.bay, p.gameGuid, p.status, p.currentGameType, p.currentGameState)
  }
}
