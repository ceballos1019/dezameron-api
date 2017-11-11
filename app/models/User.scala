package models

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.Macros._
import play.api.libs.json.{Json, Reads, Writes}

import javax.inject.Inject

import models.DataSource
import play.api.libs.json.Json
import play.api.mvc._
import utils.JWTUtil

import scala.concurrent.Future

case class User(doc_type: String, doc_id: String, email: String, phone_number: String)

object User{

  val codecUser: CodecRegistry  = fromRegistries(fromProviders(classOf[User]))

  implicit val userWrite : Writes[User] = Json.writes[User]
  implicit val userRead : Reads[User] = Json.reads[User]
}

case class UserInfo(email:String, userId:String)

case class UserRequest[A](user: User, request: Request[A]) extends WrappedRequest(request)

class SecuredAuthenticator @Inject()(dataSource: DataSource) extends AbstractController{
  implicit val formatUserDetails = Json.format[UserInfo]

  object JWTAuthentication extends ActionBuilder[UserRequest] {
    def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
      val jwtToken = request.headers.get("jw_token").getOrElse("")

      if (JwtUtility.isValidToken(jwtToken)) {
        JwtUtility.decodePayload(jwtToken).fold {
          Future.successful(Unauthorized("Invalid credential"))
        } { payload =>
          val userCredentials = Json.parse(payload).validate[UserInfo].get

          // Replace this block with data source
          val maybeUserInfo = dataSource.getUser(userCredentials.email, userCredentials.userId)

          maybeUserInfo.fold(Future.successful(Unauthorized("Invalid credential")))(user => block(UserRequest(user, request)))
        }
      } else {
        Future.successful(Unauthorized("Invalid credential"))
      }
    }
  }

}
