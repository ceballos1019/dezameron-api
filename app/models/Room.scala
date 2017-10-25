package models

import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.collection.mutable.Document
import play.api.libs.json.{JsString, JsValue, Json, Writes}




case class Room( room_id: Option[Int], hotel_id:  Option[Int], room_type:String,
                city:Option[String], capacity: Int, price:Int, currency:String, room_thumbnail:String,
                description:String, beds: Bed)

object Room{

  /*def apply( room_id:  Option[Int], hotel_id:  Option[Int], room_type:String,
             city:Option[String], capacity: Int, price:Int,
             currency:String, room_thumbnail:String,
             description:String, beds: Bed): Room =

    Room(new ObjectId(),room_id:  Option[Int], hotel_id:  Option[Int],
      room_type:String, city:Option[String], capacity: Int,
      price:Int, currency:String,room_thumbnail:String,
      description:String,  beds: Bed)*/

  //Para poder convertir la colección en BSON y viceversa

  val codecRegistry  = fromRegistries(fromProviders(classOf[Room]),Bed.codecBed,DEFAULT_CODEC_REGISTRY)
  val mongoClient: MongoClient = MongoClient("mongodb://scaladores:root@ds121945.mlab.com:21945/heroku_8bc7c40l")
  val database: MongoDatabase = mongoClient.getDatabase("heroku_8bc7c40l").withCodecRegistry(codecRegistry)
  val rooms: MongoCollection[Room] = database.getCollection("rooms")
/*
  val mongoClient: MongoClient = MongoClient()
  val database : MongoDatabase = mongoClient.getDatabase("dezamerondb").withCodecRegistry(codecRegistry)
  val rooms : MongoCollection[Room] = database.getCollection("room")*/

  /*implicit val objectIdWrites = new Writes[ObjectId] {
    def writes(oId: ObjectId): JsValue = {
      JsString(oId.toString)
    }
  }*/

  implicit val roomWrite = Json.writes[Room]
}

