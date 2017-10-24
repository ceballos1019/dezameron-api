package models

import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import play.api.libs.json.{JsString, JsValue, Json, Writes}

case class Room(_id:ObjectId, room_id: Int, hotel_id: Int, room_type:String,
                city:String, capacity: Int, price:Int, currency:String,
                description:String)

object Room{

  def apply( room_id: Int, hotel_id: Int, room_type:String,
             city:String, capacity: Int, price:Int, currency:String,
             description:String): Room =
    Room(new ObjectId(),room_id: Int, hotel_id: Int, room_type:String,
      city:String, capacity: Int, price:Int, currency:String,
      description:String)

  //Para poder convertir la colección en BSON y viceversa
  val codecRegistry  = fromRegistries(fromProviders(classOf[Room]),DEFAULT_CODEC_REGISTRY)

  val mongoClient: MongoClient = MongoClient("mongodb://heroku_8bc7c40l@ds121945.mlab.com:21945")
  val database: MongoDatabase = mongoClient.getDatabase("heroku_8bc7c40l").withCodecRegistry(codecRegistry)
  val rooms: MongoCollection[Room] = database.getCollection("rooms")

  implicit val objectIdWrites = new Writes[ObjectId] {
    def writes(oId: ObjectId): JsValue = {
      JsString(oId.toString)
    }
  }

  implicit val roomWrite = Json.writes[Room]
}