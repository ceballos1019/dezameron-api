package models


import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, Observer}
import play.api.libs.json._
import models.Helpers._

case class  Hotel(_id: ObjectId, hotel_id: Double, hotel_name: String)

object Hotel {

  def apply(hotel_id: Double, hotel_name: String): Hotel =
    Hotel(new ObjectId(),hotel_id,hotel_name)

  //Para poder convertir el modelo a BSON y viceversa
  val codecRegistry = fromRegistries(fromProviders(classOf[Hotel]), DEFAULT_CODEC_REGISTRY )
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("dezamerondb").withCodecRegistry(codecRegistry)
  val collection: MongoCollection[Hotel] = database.getCollection("hotel")

  implicit val objectIdWrites = new Writes[ObjectId] {
    def writes(oId: ObjectId): JsValue = {
      JsString(oId.toString)
    }
  }

  implicit val hotelWrite = Json.writes[Hotel]
  //implicit val hotelRead = Json.reads[Hotel]

}