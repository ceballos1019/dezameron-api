package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json._

case class HotelResponse(hotel_id: String, hotel_name: String, hotel_thumbnail:String,
                         hotel_location: Location, check_in:String, check_out:String, hotel_website:String,
                         reservation: Seq[ReservationResponse])

object HotelResponse {

  /*Codec for Reservation class*/
  val codecReservation : CodecRegistry = fromRegistries(fromProviders(classOf[HotelResponse]), DEFAULT_CODEC_REGISTRY)


  implicit val hotelResponseWrite : Writes[HotelResponse] = Json.writes[HotelResponse]
  //implicit val hotelResponseRead : Reads[HotelResponse] = Json.reads[HotelResponse]
  /*
    implicit val objectIdWrites = new Writes[ObjectId] {
      def writes(oId: ObjectId): JsValue = {
        JsString(oId.toString)
      }
    }*/

}