package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import play.api.libs.json.{Json, Reads, Writes}

case class User(doc_type: String, doc_id: String, email: String, phone_number: String)

object User{

  val codecUser: CodecRegistry  = fromRegistries(fromProviders(classOf[User]),DEFAULT_CODEC_REGISTRY)

  implicit val userWrite : Writes[User] = Json.writes[User]
  implicit val userRead : Reads[User] = Json.reads[User]
}
