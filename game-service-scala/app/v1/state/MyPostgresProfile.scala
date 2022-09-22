package v1.state

import com.github.tminglei.slickpg._
import play.api.libs.json._
import slick.jdbc.PostgresProfile

case class JBean(name: String, count: Int)
object JBean {
  implicit val jbeanFmt = Json.format[JBean]
  implicit val jbeanWrt = Json.writes[JBean]
}

trait MyPostgresProfile extends PostgresProfile
  with PgPlayJsonSupport
  with array.PgArrayJdbcTypes {
  override val pgjson = "jsonb"

  override val api: API = new API {}

  val plainAPI = new API with PlayJsonPlainImplicits

  ///
  trait API extends super.API with JsonImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val beanJsonTypeMapper = MappedJdbcType.base[JBean, JsValue](Json.toJson(_), _.as[JBean])
    implicit val jsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)
    implicit val beanArrayTypeMapper =
      new AdvancedArrayJdbcType[JBean](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JBean](Json.parse(_).as[JBean])(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JBean](b => Json.stringify(Json.toJson(b)))(v)
      ).to(_.toList)
  }
}
object MyPostgresProfile extends MyPostgresProfile
