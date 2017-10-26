package models

import models.Room.codecRegistry
import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.bson.types.ObjectId
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.{MongoClient, MongoDatabase}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import play.api.libs.json._

case class Bed(simple:Int, double:Int)

object Bed{

  val codecBed : CodecRegistry  = fromRegistries(fromProviders(classOf[Bed]),DEFAULT_CODEC_REGISTRY)

  implicit val bedWrite : Writes[Bed] = Json.writes[Bed]
  implicit val bedRead : Reads[Bed] = Json.reads[Bed]
}