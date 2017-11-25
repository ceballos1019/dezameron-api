package utils

import java.io.InputStream

import com.google.firebase.auth.{FirebaseAuth, FirebaseAuthException}
import com.google.firebase.tasks.Tasks
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import jdk.nashorn.internal.codegen.CompilationException
import play.api.PlayException

case class FirebaseException(s:String)

object Firebase {
  val credentials: InputStream = getClass.getResourceAsStream("/firebaseAdminCredentials.json");

  val AuthorizationErrorMessage = "Authorization header is empty"
  val TokenErrorMessage = "Token has expired or is not valid"


  val options = new FirebaseOptions.Builder()
    .setServiceAccount(credentials)
    .setDatabaseUrl("https://gohotels-5a589.firebaseio.com")
    .build();
  FirebaseApp.initializeApp(options);

  def verifyToken(idToken:Option[String]):String ={
    idToken match {
      case Some(idToken) =>
          try {

            var token = idToken.filterNot((x: Char) => x.isWhitespace)
            token = token.replaceAll("Bearer","")
            val decodedToken = Tasks.await (
            FirebaseAuth.getInstance ().verifyIdToken (token));
            decodedToken.getUid

          } catch {
            case e: Exception => TokenErrorMessage
          }
      case None => AuthorizationErrorMessage
    }
  }

}
