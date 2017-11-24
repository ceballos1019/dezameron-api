package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json._

case class Reservation(var reserve_id: Option[String], arrive_date: String, leave_date: String, room_type:String,
                capacity: Int, beds: Bed, hotel_id: String, user: User, var state: Option[String])

object Reservation{

  /*Codec for Reservation class*/
  val codecReservation : CodecRegistry = fromRegistries(fromProviders(classOf[Reservation]), DEFAULT_CODEC_REGISTRY)
  val CollectionName = "reservations"

  implicit val reservationWrite : Writes[Reservation] = Json.writes[Reservation]
  implicit val reservationRead : Reads[Reservation] = Json.reads[Reservation]
/*
  implicit val objectIdWrites = new Writes[ObjectId] {
    def writes(oId: ObjectId): JsValue = {
      JsString(oId.toString)
    }
  }*/

}