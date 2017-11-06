package models


import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json._


case class Location(address:String,lat:String, long:String)

object Location{
  val codecLocation  = fromRegistries(fromProviders(classOf[Location]))
  implicit val locationWrite = Json.writes[Location]
}


case class  Hotel( hotel_id: String, hotel_name: String,
                  city:Option[String] ,hotel_location: Location,hotel_thumbnail:String,
                   check_in:String, check_out:String, hotel_website:String,
                  rooms:Seq[Room])

object Hotel {
  //Para poder convertir el modelo a BSON y viceversa
  val codecHotel : CodecRegistry = fromRegistries(fromProviders(classOf[Hotel]))
  val COLLECTION_NAME : String = "hotels"

  implicit val hotelWrite = Json.writes[Hotel]

}