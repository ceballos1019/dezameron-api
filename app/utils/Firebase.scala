package utils

import java.io.InputStream

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.tasks.Tasks
import com.google.firebase.{FirebaseApp, FirebaseOptions}

case class FirebaseException(s:String)

object Firebase {
  val credentials: InputStream = getClass.getResourceAsStream("/dezameron.json");

  val options = new FirebaseOptions.Builder()
    .setServiceAccount(credentials)
    .setDatabaseUrl("https://dezameron.firebaseio.com")
    .build();
  FirebaseApp.initializeApp(options);

  def verifyToken(idToken:String):String ={
  val decodedToken = Tasks.await(
    FirebaseAuth.getInstance().verifyIdToken(idToken));
    decodedToken.getUid
  }

}
