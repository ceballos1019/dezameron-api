package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json._

case class Bed(simple:Int, double:Int)

object Bed{

  val codecBed : CodecRegistry  = fromRegistries(fromProviders(classOf[Bed]))

  implicit val bedWrite : Writes[Bed] = Json.writes[Bed]
  implicit val bedRead : Reads[Bed] = Json.reads[Bed]
}