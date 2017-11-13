package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json.{Json, Writes}

case class Room( room_id: Option[Int], hotel_id:  Option[String], room_type:String,
                city:Option[String], capacity: Int, price:Int, currency:String, room_thumbnail:String,
                description:String, beds: Bed)

object Room{

  val codecRoom : CodecRegistry  = fromRegistries(fromProviders(classOf[Room]))
  val CollectionName : String = "rooms"

  implicit val roomWrite : Writes[Room] = Json.writes[Room]
}

