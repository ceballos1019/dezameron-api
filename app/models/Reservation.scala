package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json._

case class Reservation(var reservation_id: Option[String], arrive_date: String, leave_date: String, room_type:String,
                capacity: Int, beds: Bed, hotel_id: String, user: User)

object Reservation{

  /*Codec for Reservation class*/
  val codecReservation : CodecRegistry = fromRegistries(fromProviders(classOf[Reservation]), DEFAULT_CODEC_REGISTRY)
  val CollectionName = "reservations"

  implicit val reservationWrite : Writes[Reservation] = Json.writes[Reservation]
  implicit val reservationRead : Reads[Reservation] = Json.reads[Reservation]


}