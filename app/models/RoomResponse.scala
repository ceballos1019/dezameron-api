package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json._

case class RoomResponse(room_type: String, capacity: Int,  price:Int, currency:String, room_thumbnail:String,
                        description:String, beds: Bed)

object RoomResponse {

  /*Codec for Reservation class*/
  val codecReservation : CodecRegistry = fromRegistries(fromProviders(classOf[RoomResponse]), DEFAULT_CODEC_REGISTRY)


  implicit val roomResponseWrite : Writes[RoomResponse] = Json.writes[RoomResponse]


}