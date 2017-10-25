package models


import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, Observer, bson}
import play.api.libs.json._
import models.Helpers._
import models.Room.codecRegistry

case class Location(address:String,lat:String, long:String)

object Location{
  val codecLocation  = fromRegistries(fromProviders(classOf[Location]),DEFAULT_CODEC_REGISTRY)
  implicit val locationWrite = Json.writes[Location]
}


case class  Hotel( hotel_id: String, hotel_name: String,
                  city:Option[String] ,hotel_location: Location,hotel_thumbnail:String,
                   check_in:String, check_out:String, hotel_website:String,
                  rooms:Seq[Room])

object Hotel {

 /* def apply(hotel_id: String, hotel_name: String, city: Option[String] , hotel_location: Location, hotel_thumbnail:String,
            check_in:String,check_out:String, hotel_website:String,rooms:Seq[Room]): Hotel =

    Hotel(new ObjectId,hotel_id:String,hotel_name:String,city:Option[String],
      hotel_location: Location, hotel_thumbnail:String, check_in:String,
      check_out:String, hotel_website:String,rooms:Seq[Room])*/

  //Para poder convertir el modelo a BSON y viceversa
  val codecRegistry = fromRegistries(fromProviders(classOf[Hotel]),Location.codecLocation, DEFAULT_CODEC_REGISTRY )
  val mongoClient: MongoClient = MongoClient("mongodb://scaladores:root@ds121945.mlab.com:21945/heroku_8bc7c40l")
  val database: MongoDatabase = mongoClient.getDatabase("heroku_8bc7c40l").withCodecRegistry(codecRegistry)
  val hotels: MongoCollection[Hotel] = database.getCollection("hotels")

  /*implicit val objectIdWrites = new Writes[ObjectId] {
    def writes(oId: ObjectId): JsValue = {
      JsString(oId.toString)
    }
  }*/

  implicit val hotelWrite = Json.writes[Hotel]
  //implicit val hotelRead = Json.reads[Hotel]

}