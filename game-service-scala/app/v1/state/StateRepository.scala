package v1.state

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.MarkerContext
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.CustomExecutionContext
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class StateData(id: Long, bay: Int, gameGuid: String, status: String, currentGameType: String, currentGameState: JsValue)

object StateData {
  implicit  val stateDataFormat = Json.format[StateData]
}

class StateExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait StateRespository {
  def create(data: StateData)(implicit mc: MarkerContext): Future [StateData]

  def create(bay: Int, gameGuid: String, status: String, currentGameType: String, currentGameState: JsValue): Future [StateData]

  def list()(implicit mc: MarkerContext): Future[Iterable[StateData]]

  def get(bay: Int)(implicit mc: MarkerContext): Future[StateData]

  def update(data: StateData)(implicit mc: MarkerContext): Future [StateData]
}

/**
  * A repository for states.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class StateRepositoryImpl @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: StateExecutionContext) extends StateRespository {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import MyPostgresProfile.api._
  import dbConfig._

  /**
    * Here we define the table. It will have a name of people
    */
  private class StatesTable(tag: Tag) extends Table[StateData](tag, "states") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The bay column */
    def bay = column[Int]("bay")

    /** The agts column */
    def gameGuid = column[String]("game_guid")

    /** The passive column */
    def status = column[String]("status")

    /** The cgt column */
    def currentGameType = column [String]("current_game_type")

    /** The cgs column */
    def currentGameState = column [JsValue]("current_game_state")

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the State object.
      *
      * In this case, we are simply passing the id, name and page parameters to the State case classes
      * apply and unapply methods.
      */
    def * = (id, bay, gameGuid, status, currentGameType, currentGameState) <> ((StateData.apply _).tupled, StateData.unapply)
  }

  /**
    * The starting point for all queries on the people table.
    */
  private val states = TableQuery[StatesTable]

   def get(bay: Int)(implicit mc: MarkerContext): Future[StateData]  = db.run {
      states.filter(_.bay === bay).result.head
  }

   def list()(implicit mc: MarkerContext): Future[Iterable[StateData]] = db.run{
    states.result
  }

   def create(stateData: StateData)(implicit mc: MarkerContext): Future[StateData] = db.run {
    // We create a projection of just the non-id columns, since we're not inserting a value for the id column
    (states.map(p => (p.bay, p.gameGuid, p.status, p.currentGameType, (p.currentGameState)))
      // Now define it to return the id, because we want to know what id was generated for the State
      returning states.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((fields, id) => StateData(id, fields._1, fields._2, fields._3, fields._4, fields._5))
      // And finally, insert the State into the database
      ) += ((stateData.bay, stateData.gameGuid, stateData.status, stateData.currentGameType, stateData.currentGameState):
           (Int, String, String, String, play.api.libs.json.JsValue))
  }

  /**
    * Create a State with the given information
    *
    * This is an asynchronous operation, it will return a future of the created State, which can be used to obtain the
    * id for that State.
    */
   def create(bay: Int, gameGuid: String, status: String, currentGameType: String, currentGameState: JsValue): Future[StateData] = db.run {
    // We create a projection of just the non-id columns, since we're not inserting a value for the id column
    (states.map(p => (p.bay, p.gameGuid, p.status, p.currentGameType, p.currentGameState))
      // Now define it to return the id, because we want to know what id was generated for the State
      returning states.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((fields, id) => StateData(id, fields._1, fields._2, fields._3, fields._4, fields._5))
      // And finally, insert the State into the database
      ) += (bay, gameGuid, status, currentGameType, currentGameState)
  }

   def update(data: StateData)(implicit mc: MarkerContext): Future[StateData] = db.run{
    states.filter(_.bay === data.bay).update(data)
    states.filter(_.bay === data.bay).result.head
  }

  def delete(id: Int)(implicit mc: MarkerContext): Future[Int] = db.run {
    states.filter(_.bay === id).delete
  }
}
