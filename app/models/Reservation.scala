package models

import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import play.api.libs.json._

case class Reservation(reservation_id: Int, arrive_date: String, leave_date: String, room_type:String,
                capacity: Int, beds: Bed, hotel_id: Int, user: User)

object Reservation{

    //Para poder convertir la colección en BSON y viceversa
  val codecRegistry  = fromRegistries(fromProviders(classOf[Reservation]), Bed.codecBed, User.codecUser, DEFAULT_CODEC_REGISTRY)

  val mongoClient: MongoClient = MongoClient("mongodb://scaladores:root@ds121945.mlab.com:21945/heroku_8bc7c40l")
  val database: MongoDatabase = mongoClient.getDatabase("heroku_8bc7c40l").withCodecRegistry(codecRegistry)
  val reservations: MongoCollection[Reservation] = database.getCollection("reservations")
  /*
    val mongoClient: MongoClient = MongoClient()
    val database : MongoDatabase = mongoClient.getDatabase("dezamerondb").withCodecRegistry(codecRegistry)
    val rooms : MongoCollection[Room] = database.getCollection("room")*/

  implicit val reservationWrite : Writes[Reservation] = Json.writes[Reservation]
  implicit val reservationRead : Reads[Reservation] = Json.reads[Reservation]
/*
  implicit val objectIdWrites = new Writes[ObjectId] {
    def writes(oId: ObjectId): JsValue = {
      JsString(oId.toString)
    }
  }*/


}