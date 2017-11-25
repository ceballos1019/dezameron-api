package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json.{Json, Reads, Writes}

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

case class User(doc_type: String, doc_id: String, email: String, phone_number: String)

object User{
  /*Codec for User class*/

  val codecUser: CodecRegistry  = fromRegistries(fromProviders(classOf[User]))

  implicit val userWrite : Writes[User] = Json.writes[User]
  implicit val userRead : Reads[User] = Json.reads[User]
}

