package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json._

case class ReservationResponse(state: Option[String], reserve_id: Option[String], arrive_date: String, leave_date: String,
                               room: RoomResponse)

object ReservationResponse {

  /*Codec for Reservation class*/
  val codecReservation : CodecRegistry = fromRegistries(fromProviders(classOf[ReservationResponse]), DEFAULT_CODEC_REGISTRY)

  implicit val reservationResponseWrite : Writes[ReservationResponse] = Json.writes[ReservationResponse]

}